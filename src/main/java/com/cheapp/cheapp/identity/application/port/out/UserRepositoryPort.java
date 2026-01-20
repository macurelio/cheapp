package com.cheapp.cheapp.identity.application.port.out;

import com.cheapp.cheapp.identity.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
