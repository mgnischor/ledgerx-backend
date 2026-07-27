package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA entity persisting a {@code Budget} to the {@code budgets} table.
 */
@Entity
@Table(name = "budgets")
public class BudgetJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private int periodYear;

    @Column(nullable = false)
    private int periodMonth;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Protected no-args constructor required by JPA.
     */
    protected BudgetJpaEntity() {
    }

    /**
     * Creates a new active budget entity.
     *
     * @param id the budget identifier
     * @param companyId the identifier of the company that owns the budget
     * @param categoryId the identifier of the category the budget applies to
     * @param periodYear the year of the budget period
     * @param periodMonth the month of the budget period
     * @param limitAmount the maximum amount allowed to be spent in the period
     * @param currencyCode the ISO currency code of the limit amount
     */
    public BudgetJpaEntity(UUID id, UUID companyId, UUID categoryId, int periodYear, int periodMonth,
            BigDecimal limitAmount, String currencyCode) {
        super(id);
        this.companyId = companyId;
        this.categoryId = categoryId;
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
        this.limitAmount = limitAmount;
        this.currencyCode = currencyCode;
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
     * Returns the year of the budget period.
     *
     * @return the period year
     */
    public int getPeriodYear() {
        return periodYear;
    }

    /**
     * Returns the month of the budget period.
     *
     * @return the period month
     */
    public int getPeriodMonth() {
        return periodMonth;
    }

    /**
     * Returns the maximum amount allowed to be spent in the period.
     *
     * @return the limit amount
     */
    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    /**
     * Returns the ISO currency code of the limit amount.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Returns whether the budget is currently active.
     *
     * @return {@code true} if the budget is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the budget is currently active.
     *
     * @param active the new active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
