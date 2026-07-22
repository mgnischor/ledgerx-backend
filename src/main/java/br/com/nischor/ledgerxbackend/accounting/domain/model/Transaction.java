package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final UUID financialAccountId;
    private final UUID categoryId;
    private final TransactionType type;
    private final Money amount;
    private final String description;
    private final LocalDate occurredOn;

    public Transaction(UUID id, UUID financialAccountId, UUID categoryId, TransactionType type, Money amount,
            String description, LocalDate occurredOn) {
        this.id = id;
        this.financialAccountId = financialAccountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.occurredOn = occurredOn;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFinancialAccountId() {
        return financialAccountId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getOccurredOn() {
        return occurredOn;
    }
}
