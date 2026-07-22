package br.com.nischor.ledgerxbackend.shared.domain.event;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredOn();
}
