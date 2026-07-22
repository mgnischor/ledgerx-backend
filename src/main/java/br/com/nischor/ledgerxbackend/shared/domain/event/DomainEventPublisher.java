package br.com.nischor.ledgerxbackend.shared.domain.event;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
