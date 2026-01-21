package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.UnauthorizedException;
import com.cheapp.cheapp.identity.application.port.in.AuthenticateUserUseCase;
import com.cheapp.cheapp.identity.application.port.out.JwtProviderPort;
import com.cheapp.cheapp.identity.application.port.out.PasswordEncoderPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;

import java.util.stream.Collectors;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProviderPort jwtProvider;

    public AuthenticateUserService(UserRepositoryPort userRepository,
                                   PasswordEncoderPort passwordEncoder,
                                   JwtProviderPort jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Result authenticate(Command command) {
        var user = userRepository.findByEmail(command.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!user.enabled()) {
            throw new UnauthorizedException("Usuario deshabilitado");
        }

        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        var roles = user.roles().stream().map(r -> r.name()).collect(Collectors.toUnmodifiableSet());
        var permissions = user.roles().stream()
                .flatMap(r -> r.permissions().stream())
                .map(p -> p.name())
                .collect(Collectors.toUnmodifiableSet());

        var token = jwtProvider.createToken(user.id(), user.email(), roles, permissions);

        return new Result(token.value(), "Bearer", token.expiresInSeconds(), user.id(), user.email(), roles, permissions);
    }
}
