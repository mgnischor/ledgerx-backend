package br.com.nischor.ledgerxbackend.billing.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.billing.domain.event.InvoicePaidEvent;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Bridges the in-process {@link InvoicePaidEvent} (published via {@code DomainEventPublisher} in
 * {@code RegisterPaymentUseCase}) onto the {@code ledgerx.events} RabbitMQ exchange, so other
 * services can react to fully paid invoices without coupling to this module.
 */
@Component
public class InvoicePaidEventPublisher {

    /** Template used to publish messages onto the RabbitMQ broker. */
    private final AmqpTemplate amqpTemplate;

    /**
     * Creates the publisher.
     *
     * @param amqpTemplate the AMQP template used to send messages to the exchange
     */
    public InvoicePaidEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * Handles the in-process {@link InvoicePaidEvent} and forwards it to the
     * {@code ledgerx.events} exchange using the invoice-paid routing key.
     *
     * @param event the domain event describing the invoice that was fully paid
     */
    @EventListener
    public void onInvoicePaid(InvoicePaidEvent event) {
        amqpTemplate.convertAndSend(RabbitMqConfig.EVENTS_EXCHANGE, RabbitMqConfig.ROUTING_KEY_INVOICE_PAID, event);
    }
}
