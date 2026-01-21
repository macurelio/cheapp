package com.cheapp.cheapp.identity.adapters.out.persistence.jpa;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.mapper.IdentityJpaMapper;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository.RoleSpringDataRepository;
import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Role;

import java.util.List;
import java.util.Optional;

public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleSpringDataRepository roleRepository;

    public RoleRepositoryAdapter(RoleSpringDataRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name).map(IdentityJpaMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll().stream().map(IdentityJpaMapper::toDomain).toList();
    }
}
