package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.in.AssignRoleUseCase;
import com.cheapp.cheapp.identity.application.port.out.EventPublisherPort;
import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.event.RoleAssignedEvent;

public class AssignRoleService implements AssignRoleUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final EventPublisherPort eventPublisher;

    public AssignRoleService(UserRepositoryPort userRepository,
                             RoleRepositoryPort roleRepository,
                             EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void assign(Command command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        var role = roleRepository.findByName(command.roleName())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));

        var newRoles = new java.util.LinkedHashSet<>(user.roles());
        newRoles.add(role);

        userRepository.save(user.withRoles(newRoles));
        eventPublisher.publish(RoleAssignedEvent.now(user.id(), role.name()));
    }
}
