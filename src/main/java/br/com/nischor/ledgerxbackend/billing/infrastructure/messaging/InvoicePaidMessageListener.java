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

    private final CreateNotificationUseCase createNotificationUseCase;

    public InvoicePaidMessageListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_INVOICE_PAID)
    public void onMessage(InvoicePaidEvent event) {
        log.info("Received InvoicePaidEvent: invoice {} for party {}", event.invoiceId(), event.partyId());
        createNotificationUseCase.execute(NotificationType.INVOICE_PAID, event.invoiceId(),
                "Invoice %s fully paid by party %s".formatted(event.invoiceId(), event.partyId()));
    }
}
