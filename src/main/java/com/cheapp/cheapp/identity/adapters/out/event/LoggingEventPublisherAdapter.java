package com.cheapp.cheapp.identity.adapters.out.event;

import com.cheapp.cheapp.identity.application.port.out.EventPublisherPort;
import com.cheapp.cheapp.identity.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisherAdapter.class);

    @Override
    public void publish(DomainEvent event) {
        // MVP: publicamos a log (luego se puede migrar a outbox/Kafka/etc.)
        log.info("DomainEvent published: {}", event);
    }
}
