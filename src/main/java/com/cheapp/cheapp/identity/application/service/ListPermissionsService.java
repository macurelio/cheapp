package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.port.in.ListPermissionsUseCase;
import com.cheapp.cheapp.identity.application.port.out.PermissionRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Permission;

import java.util.List;

public class ListPermissionsService implements ListPermissionsUseCase {

    private final PermissionRepositoryPort permissionRepository;

    public ListPermissionsService(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> list() {
        return permissionRepository.findAll();
    }
}
