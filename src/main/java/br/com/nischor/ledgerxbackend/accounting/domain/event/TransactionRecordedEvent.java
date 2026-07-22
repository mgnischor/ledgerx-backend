package br.com.nischor.ledgerxbackend.accounting.domain.event;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRecordedEvent(UUID transactionId, UUID financialAccountId, TransactionType type,
        BigDecimal amount, Instant occurredOn) implements DomainEvent {

    public TransactionRecordedEvent(UUID transactionId, UUID financialAccountId, TransactionType type,
            BigDecimal amount) {
        this(transactionId, financialAccountId, type, amount, Instant.now());
    }
}
