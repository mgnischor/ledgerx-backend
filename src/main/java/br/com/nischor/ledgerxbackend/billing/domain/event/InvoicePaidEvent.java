package br.com.nischor.ledgerxbackend.billing.domain.event;

import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record InvoicePaidEvent(UUID invoiceId, UUID partyId, Instant occurredOn) implements DomainEvent {

    public InvoicePaidEvent(UUID invoiceId, UUID partyId) {
        this(invoiceId, partyId, Instant.now());
    }
}
