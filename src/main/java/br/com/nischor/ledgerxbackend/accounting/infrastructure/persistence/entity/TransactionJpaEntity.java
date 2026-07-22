package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

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
@Table(name = "transactions")
public class TransactionJpaEntity extends AuditableEntity {

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
    private String description;

    @Column(nullable = false)
    private LocalDate occurredOn;

    protected TransactionJpaEntity() {
    }

    public TransactionJpaEntity(UUID financialAccountId, UUID categoryId, TransactionType type, BigDecimal amount,
            String description, LocalDate occurredOn) {
        this.financialAccountId = financialAccountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.occurredOn = occurredOn;
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

    public String getDescription() {
        return description;
    }

    public LocalDate getOccurredOn() {
        return occurredOn;
    }
}
