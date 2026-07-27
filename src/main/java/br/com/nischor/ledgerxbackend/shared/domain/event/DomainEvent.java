package br.com.nischor.ledgerxbackend.shared.domain.event;

import java.time.Instant;

/**
 * Marker contract for domain events raised by aggregates within the domain layer.
 *
 * <p>Domain events represent facts that happened in the past and are used to
 * communicate state changes to other parts of the system without coupling
 * the domain layer to infrastructure concerns.
 */
public interface DomainEvent {

    /**
     * Returns the instant at which this event occurred.
     *
     * @return the timestamp of the event occurrence
     */
    Instant occurredOn();
}
