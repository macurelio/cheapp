package com.cheapp.cheapp.identity.application.port.out;

import com.cheapp.cheapp.identity.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String name);

    List<Role> findAll();
}
