package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;

public class RecurringTransactionRule {

    private final UUID id;
    private final UUID companyId;
    private final UUID financialAccountId;
    private final UUID categoryId;
    private final TransactionType type;
    private final Money amount;
    private final String description;
    private final RecurrenceFrequency frequency;
    private LocalDate nextOccurrence;
    private boolean active;

    public RecurringTransactionRule(UUID id, UUID companyId, UUID financialAccountId, UUID categoryId,
            TransactionType type, Money amount, String description, RecurrenceFrequency frequency,
            LocalDate nextOccurrence) {
        this.id = id;
        this.companyId = companyId;
        this.financialAccountId = financialAccountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.frequency = frequency;
        this.nextOccurrence = nextOccurrence;
        this.active = true;
    }

    public boolean isDue(LocalDate today) {
        return active && !nextOccurrence.isAfter(today);
    }

    public void advance() {
        this.nextOccurrence = switch (frequency) {
            case WEEKLY -> nextOccurrence.plusWeeks(1);
            case MONTHLY -> nextOccurrence.plusMonths(1);
            case YEARLY -> nextOccurrence.plusYears(1);
        };
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
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

    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    public LocalDate getNextOccurrence() {
        return nextOccurrence;
    }

    public boolean isActive() {
        return active;
    }
}
