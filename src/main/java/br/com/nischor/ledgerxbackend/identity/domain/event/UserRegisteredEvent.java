package br.com.nischor.ledgerxbackend.identity.domain.event;

import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(UUID userId, String email, Instant occurredOn) implements DomainEvent {

    public UserRegisteredEvent(UUID userId, String email) {
        this(userId, email, Instant.now());
    }
}
