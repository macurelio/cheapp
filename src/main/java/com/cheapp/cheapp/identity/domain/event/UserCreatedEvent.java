package com.cheapp.cheapp.identity.domain.event;

import java.time.Instant;

public record UserCreatedEvent(Long userId, String email, Instant occurredAt) implements DomainEvent {

    public static UserCreatedEvent now(Long userId, String email) {
        return new UserCreatedEvent(userId, email, Instant.now());
    }
}
