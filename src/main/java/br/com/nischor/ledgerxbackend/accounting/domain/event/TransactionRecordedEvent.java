package br.com.nischor.ledgerxbackend.accounting.domain.event;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published whenever a financial transaction is recorded.
 *
 * @param transactionId the identifier of the recorded transaction
 * @param financialAccountId the identifier of the financial account the transaction was posted to
 * @param type the transaction type (income or expense)
 * @param amount the transaction amount
 * @param occurredOn the instant at which the event was created
 */
public record TransactionRecordedEvent(UUID transactionId, UUID financialAccountId, TransactionType type,
        BigDecimal amount, Instant occurredOn) implements DomainEvent {

    /**
     * Creates the event with the current instant as its occurrence time.
     *
     * @param transactionId the identifier of the recorded transaction
     * @param financialAccountId the identifier of the financial account the transaction was posted to
     * @param type the transaction type (income or expense)
     * @param amount the transaction amount
     */
    public TransactionRecordedEvent(UUID transactionId, UUID financialAccountId, TransactionType type,
            BigDecimal amount) {
        this(transactionId, financialAccountId, type, amount, Instant.now());
    }
}
