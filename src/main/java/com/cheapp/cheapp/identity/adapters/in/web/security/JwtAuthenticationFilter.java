package com.cheapp.cheapp.identity.adapters.in.web.security;

import com.cheapp.cheapp.identity.application.port.out.JwtProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProviderPort jwtProvider;

    public JwtAuthenticationFilter(JwtProviderPort jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            var token = header.substring("Bearer ".length());
            try {
                var decoded = jwtProvider.decodeAndValidate(token);
                var authorities = decoded.roles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toSet());

                var auth = new UsernamePasswordAuthenticationToken(decoded.email(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
                // Token inválido: dejamos que Security maneje la falta de auth
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
