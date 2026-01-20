package com.cheapp.cheapp.identity.application.port.out;

import com.cheapp.cheapp.identity.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
