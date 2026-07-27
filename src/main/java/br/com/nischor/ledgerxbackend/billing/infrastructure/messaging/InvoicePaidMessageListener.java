package br.com.nischor.ledgerxbackend.billing.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.billing.domain.event.InvoicePaidEvent;
import br.com.nischor.ledgerxbackend.notification.application.usecase.CreateNotificationUseCase;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code ledgerx.events.invoice-paid} and turns it into a persisted
 * {@link br.com.nischor.ledgerxbackend.notification.domain.model.Notification}, proving the queue
 * is wired end-to-end.
 */
@Component
public class InvoicePaidMessageListener {

    private static final Logger log = LoggerFactory.getLogger(InvoicePaidMessageListener.class);

    /** Use case that persists the resulting notification. */
    private final CreateNotificationUseCase createNotificationUseCase;

    /**
     * Creates the listener.
     *
     * @param createNotificationUseCase use case invoked to persist a notification for the event
     */
    public InvoicePaidMessageListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    /**
     * Handles an {@link InvoicePaidEvent} message consumed from the invoice-paid queue by
     * creating a corresponding {@code INVOICE_PAID} notification.
     *
     * @param event the invoice-paid event received from the queue
     */
    @RabbitListener(queues = RabbitMqConfig.QUEUE_INVOICE_PAID)
    public void onMessage(InvoicePaidEvent event) {
        log.info("Received InvoicePaidEvent: invoice {} for party {}", event.invoiceId(), event.partyId());
        createNotificationUseCase.execute(NotificationType.INVOICE_PAID, event.invoiceId(),
                "Invoice %s fully paid by party %s".formatted(event.invoiceId(), event.partyId()));
    }
}
