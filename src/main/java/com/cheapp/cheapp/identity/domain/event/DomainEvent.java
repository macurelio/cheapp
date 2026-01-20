package com.cheapp.cheapp.identity.domain.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}
