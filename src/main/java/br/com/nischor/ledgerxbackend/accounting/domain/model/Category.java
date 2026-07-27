package br.com.nischor.ledgerxbackend.accounting.domain.model;

import java.util.UUID;

/**
 * Domain entity representing a category used to classify financial transactions as income or expense.
 */
public class Category {

    private final UUID id;
    private final UUID companyId;
    private String name;
    private TransactionType type;

    /**
     * Creates a new category.
     *
     * @param id the category identifier
     * @param companyId the identifier of the company that owns the category
     * @param name the category name
     * @param type the transaction type (income or expense) the category is associated with
     */
    public Category(UUID id, UUID companyId, String name, TransactionType type) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.type = type;
    }

    /**
     * Renames the category.
     *
     * @param name the new category name
     */
    public void rename(String name) {
        this.name = name;
    }

    /**
     * Returns the category identifier.
     *
     * @return the category identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the identifier of the company that owns the category.
     *
     * @return the company identifier
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * Returns the category name.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the transaction type the category is associated with.
     *
     * @return the transaction type
     */
    public TransactionType getType() {
        return type;
    }
}
