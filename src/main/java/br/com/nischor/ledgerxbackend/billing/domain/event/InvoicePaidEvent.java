package br.com.nischor.ledgerxbackend.billing.domain.event;

import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event signaling that an invoice has been fully paid.
 *
 * @param invoiceId  the identifier of the invoice that was paid.
 * @param partyId    the identifier of the counterparty of the invoice.
 * @param occurredOn the instant the event occurred.
 */
public record InvoicePaidEvent(UUID invoiceId, UUID partyId, Instant occurredOn) implements DomainEvent {

    /**
     * Creates the event with the occurrence instant set to now.
     *
     * @param invoiceId the identifier of the invoice that was paid.
     * @param partyId   the identifier of the counterparty of the invoice.
     */
    public InvoicePaidEvent(UUID invoiceId, UUID partyId) {
        this(invoiceId, partyId, Instant.now());
    }
}
