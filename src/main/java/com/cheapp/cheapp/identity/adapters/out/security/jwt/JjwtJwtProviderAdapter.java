package com.cheapp.cheapp.identity.adapters.out.security.jwt;

import com.cheapp.cheapp.identity.application.port.out.JwtProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

public class JjwtJwtProviderAdapter implements JwtProviderPort {

    private final JwtProperties props;
    private final SecretKey key;

    public JjwtJwtProviderAdapter(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Token createToken(Long userId, String email, Set<String> roles, Set<String> permissions) {
        var now = Instant.now();
        var exp = now.plusSeconds(props.ttlSeconds());

        var jwt = Jwts.builder()
                .issuer(props.issuer())
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", email)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .signWith(key)
                .compact();

        return new Token(jwt, props.ttlSeconds());
    }

    @Override
    public DecodedToken decodeAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);

        @SuppressWarnings("unchecked")
        Set<String> roles = Set.copyOf((Collection<String>) claims.get("roles"));

        Set<String> permissions;
        Object permsClaim = claims.get("permissions");
        if (permsClaim == null) {
            permissions = Set.of();
        } else {
            @SuppressWarnings("unchecked")
            Collection<String> raw = (Collection<String>) permsClaim;
            permissions = Set.copyOf(raw);
        }

        return new DecodedToken(userId, email, roles, permissions);
    }
}
