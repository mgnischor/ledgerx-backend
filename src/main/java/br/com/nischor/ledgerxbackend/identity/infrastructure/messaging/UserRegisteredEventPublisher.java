package br.com.nischor.ledgerxbackend.identity.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.identity.domain.event.UserRegisteredEvent;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Bridges the in-process {@link UserRegisteredEvent} (published via {@code DomainEventPublisher}
 * in {@code RegisterUserUseCase}) onto the {@code ledgerx.events} RabbitMQ exchange, so other
 * services can react to user registration without coupling to this module.
 */
@Component
public class UserRegisteredEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public UserRegisteredEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        amqpTemplate.convertAndSend(RabbitMqConfig.EVENTS_EXCHANGE, RabbitMqConfig.ROUTING_KEY_USER_REGISTERED,
                event);
    }
}
