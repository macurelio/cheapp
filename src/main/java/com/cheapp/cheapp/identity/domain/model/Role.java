package com.cheapp.cheapp.identity.domain.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Role {

    private final Long id;
    private final String name;
    private final Set<Permission> permissions;

    public Role(Long id, String name, Set<Permission> permissions) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.permissions = permissions == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(permissions));
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Set<Permission> permissions() {
        return permissions;
    }
}
