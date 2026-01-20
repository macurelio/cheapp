package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.in.UpdateUserUseCase;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.User;

public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepositoryPort userRepository;

    public UpdateUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void update(Command command) {
        var user = userRepository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        var newEmail = command.email() == null ? user.email() : command.email().trim().toLowerCase();
        if (!newEmail.equals(user.email()) && userRepository.existsByEmail(newEmail)) {
            throw new ConflictException("El email ya está registrado");
        }

        Boolean enabled = command.enabled() == null ? user.enabled() : command.enabled();

        var updated = new User(user.id(), newEmail, user.passwordHash(), enabled, user.roles(), user.createdAt());
        userRepository.save(updated);
    }
}
