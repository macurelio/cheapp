package com.cheapp.cheapp.identity.adapters.out.persistence.jpa.mapper;

import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.RoleJpaEntity;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.entity.UserJpaEntity;
import com.cheapp.cheapp.identity.domain.model.Permission;
import com.cheapp.cheapp.identity.domain.model.Role;
import com.cheapp.cheapp.identity.domain.model.User;

import java.util.LinkedHashSet;
import java.util.Set;

public final class IdentityJpaMapper {

    private IdentityJpaMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        Set<Role> roles = new LinkedHashSet<>();
        for (RoleJpaEntity r : entity.getRoles()) {
            roles.add(toDomain(r));
        }

        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.isEnabled(),
                roles,
                entity.getCreatedAt()
        );
    }

    public static Role toDomain(RoleJpaEntity entity) {
        var perms = new LinkedHashSet<Permission>();
        if (entity.getPermissions() != null) {
            entity.getPermissions().forEach(p -> perms.add(new Permission(p.getId(), p.getName())));
        }
        return new Role(entity.getId(), entity.getName(), perms);
    }

    public static void updateUserEntityFromDomain(User domain, UserJpaEntity entity, Set<RoleJpaEntity> roles) {
        entity.setEmail(domain.email());
        entity.setPasswordHash(domain.passwordHash());
        entity.setEnabled(domain.enabled());
        if (roles != null) {
            entity.setRoles(roles);
        }
    }
}
