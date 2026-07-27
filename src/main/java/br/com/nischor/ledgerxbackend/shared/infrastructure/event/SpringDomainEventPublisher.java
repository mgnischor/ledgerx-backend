package br.com.nischor.ledgerxbackend.shared.infrastructure.event;

import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements the {@link DomainEventPublisher} port by delegating to Spring's
 * {@link ApplicationEventPublisher}, allowing domain events to be consumed by Spring
 * {@code @EventListener} / {@code @TransactionalEventListener} methods.
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Creates a new publisher backed by the given Spring application event publisher.
     *
     * @param applicationEventPublisher the Spring publisher used to dispatch events
     */
    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Publishes the given domain event through the underlying Spring application context.
     *
     * @param event the domain event to publish
     */
    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
