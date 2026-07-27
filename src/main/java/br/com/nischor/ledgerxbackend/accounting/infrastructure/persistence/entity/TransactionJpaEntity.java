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

/**
 * JPA entity persisting a {@code Transaction} to the {@code transactions} table.
 */
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

    /**
     * Protected no-args constructor required by JPA.
     */
    protected TransactionJpaEntity() {
    }

    /**
     * Creates a new transaction entity.
     *
     * @param id the transaction identifier
     * @param financialAccountId the identifier of the financial account the transaction is posted to
     * @param categoryId the identifier of the category the transaction belongs to
     * @param type the transaction type (income or expense)
     * @param amount the transaction amount
     * @param description a free-text description of the transaction
     * @param occurredOn the date the transaction occurred
     */
    public TransactionJpaEntity(UUID id, UUID financialAccountId, UUID categoryId, TransactionType type,
            BigDecimal amount, String description, LocalDate occurredOn) {
        super(id);
        this.financialAccountId = financialAccountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.occurredOn = occurredOn;
    }

    /**
     * Returns the identifier of the financial account the transaction is posted to.
     *
     * @return the financial account identifier
     */
    public UUID getFinancialAccountId() {
        return financialAccountId;
    }

    /**
     * Returns the identifier of the category the transaction belongs to.
     *
     * @return the category identifier
     */
    public UUID getCategoryId() {
        return categoryId;
    }

    /**
     * Returns the transaction type.
     *
     * @return the transaction type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Returns the transaction amount.
     *
     * @return the transaction amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the free-text description of the transaction.
     *
     * @return the transaction description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date the transaction occurred.
     *
     * @return the occurrence date
     */
    public LocalDate getOccurredOn() {
        return occurredOn;
    }
}
