package com.cheapp.cheapp.identity.adapters.out.persistence.jpa;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository.PermissionSpringDataRepository;
import com.cheapp.cheapp.identity.application.port.out.PermissionRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Permission;

import java.util.List;

public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionSpringDataRepository permissionRepository;

    public PermissionRepositoryAdapter(PermissionSpringDataRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll().stream()
                .map(e -> new Permission(e.getId(), e.getName()))
                .toList();
    }
}
