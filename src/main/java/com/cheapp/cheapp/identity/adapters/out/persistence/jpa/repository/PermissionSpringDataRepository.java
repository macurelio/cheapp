package com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionSpringDataRepository extends JpaRepository<PermissionJpaEntity, Long> {
}
