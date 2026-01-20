package com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleSpringDataRepository extends JpaRepository<RoleJpaEntity, Long> {
    Optional<RoleJpaEntity> findByName(String name);
}
