package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain entity representing a single financial transaction posted to a financial account.
 */
public class Transaction {

    private final UUID id;
    private final UUID financialAccountId;
    private final UUID categoryId;
    private final TransactionType type;
    private final Money amount;
    private final String description;
    private final LocalDate occurredOn;

    /**
     * Creates a new transaction.
     *
     * @param id the transaction identifier
     * @param financialAccountId the identifier of the financial account the transaction is posted to
     * @param categoryId the identifier of the category the transaction belongs to
     * @param type the transaction type (income or expense)
     * @param amount the transaction amount
     * @param description a free-text description of the transaction
     * @param occurredOn the date the transaction occurred
     */
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

    /**
     * Returns the transaction identifier.
     *
     * @return the transaction identifier
     */
    public UUID getId() {
        return id;
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
    public Money getAmount() {
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
