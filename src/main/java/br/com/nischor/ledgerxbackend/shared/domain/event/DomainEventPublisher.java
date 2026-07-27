package br.com.nischor.ledgerxbackend.shared.domain.event;

/**
 * Port used by the domain layer to publish {@link DomainEvent} instances
 * without depending on any specific messaging or event-bus infrastructure.
 */
public interface DomainEventPublisher {

    /**
     * Publishes the given domain event to interested subscribers.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);
}
