package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase;
import com.cheapp.cheapp.identity.application.port.out.EventPublisherPort;
import com.cheapp.cheapp.identity.application.port.out.PasswordEncoderPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.event.UserCreatedEvent;
import com.cheapp.cheapp.identity.domain.model.User;

import java.time.Instant;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EventPublisherPort eventPublisher;

    public CreateUserService(UserRepositoryPort userRepository,
                             PasswordEncoderPort passwordEncoder,
                             EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Result create(Command command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new ConflictException("El email ya está registrado");
        }

        var user = new User(
                null,
                command.email().trim().toLowerCase(),
                passwordEncoder.encode(command.password()),
                true,
                null,
                Instant.now()
        );

        var saved = userRepository.save(user);
        eventPublisher.publish(UserCreatedEvent.now(saved.id(), saved.email()));

        return new Result(saved.id(), saved.email());
    }
}
