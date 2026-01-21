package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.port.in.ListRolesUseCase;
import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Role;

import java.util.List;

public class ListRolesService implements ListRolesUseCase {

    private final RoleRepositoryPort roleRepository;

    public ListRolesService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> list() {
        return roleRepository.findAll();
    }
}
