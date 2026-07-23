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

    private final AmqpTemplate amqpTemplate;

    public InvoicePaidEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @EventListener
    public void onInvoicePaid(InvoicePaidEvent event) {
        amqpTemplate.convertAndSend(RabbitMqConfig.EVENTS_EXCHANGE, RabbitMqConfig.ROUTING_KEY_INVOICE_PAID, event);
    }
}
