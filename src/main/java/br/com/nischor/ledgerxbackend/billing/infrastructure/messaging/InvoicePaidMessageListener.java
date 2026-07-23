package br.com.nischor.ledgerxbackend.billing.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.billing.domain.event.InvoicePaidEvent;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code ledgerx.events.invoice-paid}, proving the queue is wired end-to-end. Replace the
 * log line with real logic (e.g. notifying the party or issuing a receipt) when that becomes a
 * requirement.
 */
@Component
public class InvoicePaidMessageListener {

    private static final Logger log = LoggerFactory.getLogger(InvoicePaidMessageListener.class);

    @RabbitListener(queues = RabbitMqConfig.QUEUE_INVOICE_PAID)
    public void onMessage(InvoicePaidEvent event) {
        log.info("Received InvoicePaidEvent: invoice {} for party {}", event.invoiceId(), event.partyId());
    }
}
