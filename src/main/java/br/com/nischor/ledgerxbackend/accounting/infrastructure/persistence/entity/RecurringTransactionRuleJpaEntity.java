package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recurring_transaction_rules")
public class RecurringTransactionRuleJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID financialAccountId;

    @Column(nullable = false)
    private UUID categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceFrequency frequency;

    @Column(nullable = false)
    private LocalDate nextOccurrence;

    @Column(nullable = false)
    private boolean active = true;

    protected RecurringTransactionRuleJpaEntity() {
    }

    public RecurringTransactionRuleJpaEntity(UUID id, UUID companyId, UUID financialAccountId, UUID categoryId,
            TransactionType type, BigDecimal amount, String currencyCode, String description,
            RecurrenceFrequency frequency, LocalDate nextOccurrence) {
        super(id);
        this.companyId = companyId;
        this.financialAccountId = financialAccountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.description = description;
        this.frequency = frequency;
        this.nextOccurrence = nextOccurrence;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
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

    public void setActive(boolean active) {
        this.active = active;
    }
}
