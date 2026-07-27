package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Domain entity representing a spending limit set for a category during a specific month.
 */
public class Budget {

    private final UUID id;
    private final UUID companyId;
    private final UUID categoryId;
    private final YearMonth period;
    /** The maximum amount allowed to be spent in the period. */
    private Money limit;
    /** Whether the budget is currently active. */
    private boolean active;

    /**
     * Creates a new active budget.
     *
     * @param id the budget identifier
     * @param companyId the identifier of the company that owns the budget
     * @param categoryId the identifier of the category the budget applies to
     * @param period the year and month the budget covers
     * @param limit the maximum amount allowed to be spent in the period
     */
    public Budget(UUID id, UUID companyId, UUID categoryId, YearMonth period, Money limit) {
        this.id = id;
        this.companyId = companyId;
        this.categoryId = categoryId;
        this.period = period;
        this.limit = limit;
        this.active = true;
    }

    /**
     * Updates the spending limit for this budget.
     *
     * @param limit the new maximum amount allowed to be spent in the period
     */
    public void revise(Money limit) {
        this.limit = limit;
    }

    /**
     * Marks the budget as inactive.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Returns the budget identifier.
     *
     * @return the budget identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the identifier of the company that owns the budget.
     *
     * @return the company identifier
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * Returns the identifier of the category the budget applies to.
     *
     * @return the category identifier
     */
    public UUID getCategoryId() {
        return categoryId;
    }

    /**
     * Returns the year and month the budget covers.
     *
     * @return the budget period
     */
    public YearMonth getPeriod() {
        return period;
    }

    /**
     * Returns the maximum amount allowed to be spent in the period.
     *
     * @return the budget limit
     */
    public Money getLimit() {
        return limit;
    }

    /**
     * Returns whether the budget is currently active.
     *
     * @return {@code true} if the budget is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }
}
