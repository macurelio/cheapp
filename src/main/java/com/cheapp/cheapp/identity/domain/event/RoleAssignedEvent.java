package com.cheapp.cheapp.identity.domain.event;

import java.time.Instant;

public record RoleAssignedEvent(Long userId, String roleName, Instant occurredAt) implements DomainEvent {

    public static RoleAssignedEvent now(Long userId, String roleName) {
        return new RoleAssignedEvent(userId, roleName, Instant.now());
    }
}
