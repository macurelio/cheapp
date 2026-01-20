package com.cheapp.cheapp.identity.adapters.out.persistence.jpa;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.RoleJpaEntity;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.UserJpaEntity;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.mapper.IdentityJpaMapper;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository.RoleSpringDataRepository;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository.UserSpringDataRepository;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.User;

import java.util.LinkedHashSet;
import java.util.Optional;

public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserSpringDataRepository userRepository;
    private final RoleSpringDataRepository roleRepository;

    public UserRepositoryAdapter(UserSpringDataRepository userRepository, RoleSpringDataRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;
        if (user.id() == null) {
            entity = new UserJpaEntity();
        } else {
            entity = userRepository.findById(user.id()).orElseGet(UserJpaEntity::new);
            entity.setId(user.id());
        }

        var roles = new LinkedHashSet<RoleJpaEntity>();
        for (var role : user.roles()) {
            if (role.id() != null) {
                roleRepository.findById(role.id()).ifPresent(roles::add);
            } else {
                roleRepository.findByName(role.name()).ifPresent(roles::add);
            }
        }

        IdentityJpaMapper.updateUserEntityFromDomain(user, entity, roles);
        var saved = userRepository.save(entity);
        return IdentityJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(IdentityJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(IdentityJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
