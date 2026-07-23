package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

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

    protected BudgetJpaEntity() {
    }

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

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public int getPeriodYear() {
        return periodYear;
    }

    public int getPeriodMonth() {
        return periodMonth;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
