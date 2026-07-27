package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA entity persisting a {@code FinancialAccount} to the {@code financial_accounts} table.
 */
@Entity
@Table(name = "financial_accounts")
public class FinancialAccountJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Protected no-args constructor required by JPA.
     */
    protected FinancialAccountJpaEntity() {
    }

    /**
     * Creates a new active financial account entity.
     *
     * @param id the financial account identifier
     * @param companyId the identifier of the company that owns the account
     * @param name the account name
     * @param balance the account balance
     * @param currencyCode the ISO currency code of the balance
     */
    public FinancialAccountJpaEntity(UUID id, UUID companyId, String name, BigDecimal balance, String currencyCode) {
        super(id);
        this.companyId = companyId;
        this.name = name;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    /**
     * Returns the identifier of the company that owns the account.
     *
     * @return the company identifier
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * Returns the account name.
     *
     * @return the account name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current account balance.
     *
     * @return the account balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Returns the ISO currency code of the balance.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Returns whether the account is currently active.
     *
     * @return {@code true} if the account is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the account is currently active.
     *
     * @param active the new active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
