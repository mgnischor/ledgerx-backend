package br.com.nischor.ledgerxbackend.accounting.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.accounting.domain.event.TransactionRecordedEvent;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Bridges the in-process {@link TransactionRecordedEvent} (published via
 * {@code DomainEventPublisher} in {@code RecordTransactionUseCase}) onto the
 * {@code ledgerx.events} RabbitMQ exchange, so other services can react to recorded transactions
 * without coupling to this module.
 */
@Component
public class TransactionRecordedEventPublisher {

    private final AmqpTemplate amqpTemplate;

    /**
     * Creates the publisher.
     *
     * @param amqpTemplate template used to send messages to RabbitMQ
     */
    public TransactionRecordedEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * Listens for in-process {@link TransactionRecordedEvent}s and republishes them onto the
     * {@code ledgerx.events} RabbitMQ exchange.
     *
     * @param event the domain event to publish
     */
    @EventListener
    public void onTransactionRecorded(TransactionRecordedEvent event) {
        amqpTemplate.convertAndSend(RabbitMqConfig.EVENTS_EXCHANGE, RabbitMqConfig.ROUTING_KEY_TRANSACTION_RECORDED,
                event);
    }
}
