package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.in.GetUserPermissionsUseCase;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;

import java.util.Set;
import java.util.stream.Collectors;

public class GetUserPermissionsService implements GetUserPermissionsUseCase {

    private final UserRepositoryPort userRepository;

    public GetUserPermissionsService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Set<String> getEffectivePermissions(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return user.roles().stream()
                .flatMap(r -> r.permissions().stream())
                .map(p -> p.name())
                .collect(Collectors.toUnmodifiableSet());
    }
}
