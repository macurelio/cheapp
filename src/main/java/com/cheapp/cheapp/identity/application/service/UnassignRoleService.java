package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.in.UnassignRoleUseCase;
import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UnassignRoleService implements UnassignRoleUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    public UnassignRoleService(UserRepositoryPort userRepository, RoleRepositoryPort roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void unassign(Command command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        var role = roleRepository.findByName(command.roleName())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));

        var newRoles = new LinkedHashSet<>(user.roles());
        boolean removed = newRoles.removeIf(r -> r.name().equals(role.name()));
        if (!removed) {
            // nothing to do
            return;
        }

        userRepository.save(user.withRoles(newRoles.stream().collect(Collectors.toUnmodifiableSet())));
    }
}
