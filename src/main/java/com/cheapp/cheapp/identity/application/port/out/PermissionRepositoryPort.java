package com.cheapp.cheapp.identity.application.port.out;

import com.cheapp.cheapp.identity.domain.model.Permission;

import java.util.List;

public interface PermissionRepositoryPort {
    List<Permission> findAll();
}
