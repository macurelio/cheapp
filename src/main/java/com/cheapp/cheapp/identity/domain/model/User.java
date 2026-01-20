package com.cheapp.cheapp.identity.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class User {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<Role> roles;
    private final Instant createdAt;

    public User(Long id,
                String email,
                String passwordHash,
                boolean enabled,
                Set<Role> roles,
                Instant createdAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.enabled = enabled;
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(roles));
        this.createdAt = createdAt;
    }

    public Long id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public boolean enabled() {
        return enabled;
    }

    public Set<Role> roles() {
        return roles;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public User withId(Long id) {
        return new User(id, email, passwordHash, enabled, roles, createdAt);
    }

    public User withRoles(Set<Role> roles) {
        return new User(id, email, passwordHash, enabled, roles, createdAt);
    }
}
