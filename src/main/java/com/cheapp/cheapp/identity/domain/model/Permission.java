package com.cheapp.cheapp.identity.domain.model;

import java.util.Objects;

public final class Permission {

    private final Long id;
    private final String name;

    public Permission(Long id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }
}
