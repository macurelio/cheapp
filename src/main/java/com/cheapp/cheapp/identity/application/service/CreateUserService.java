package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase;
import com.cheapp.cheapp.identity.application.port.out.EventPublisherPort;
import com.cheapp.cheapp.identity.application.port.out.PasswordEncoderPort;
import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.event.UserCreatedEvent;
import com.cheapp.cheapp.identity.domain.model.User;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EventPublisherPort eventPublisher;

    public CreateUserService(UserRepositoryPort userRepository,
                             RoleRepositoryPort roleRepository,
                             PasswordEncoderPort passwordEncoder,
                             EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Result create(Command command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new ConflictException("El email ya está registrado");
        }

        Set<com.cheapp.cheapp.identity.domain.model.Role> roles = Set.of();
        if (command.roleNames() != null && !command.roleNames().isEmpty()) {
            var resolved = new LinkedHashSet<com.cheapp.cheapp.identity.domain.model.Role>();
            for (var roleName : command.roleNames()) {
                var role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + roleName));
                resolved.add(role);
            }
            roles = Set.copyOf(resolved);
        }

        var user = new User(
                null,
                command.email().trim().toLowerCase(),
                passwordEncoder.encode(command.password()),
                true,
                roles,
                Instant.now()
        );

        var saved = userRepository.save(user);
        eventPublisher.publish(UserCreatedEvent.now(saved.id(), saved.email()));

        return new Result(saved.id(), saved.email());
    }
}
