package com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSpringDataRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
