package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;

/**
 * Domain entity representing a financial account that holds a monetary balance and can be credited or debited.
 */
public class FinancialAccount {

    private final UUID id;
    private final UUID companyId;
    private String name;
    /** The current account balance. */
    private Money balance;
    /** Whether the account is currently active. */
    private boolean active;

    /**
     * Creates a new active financial account with the given opening balance.
     *
     * @param id the financial account identifier
     * @param companyId the identifier of the company that owns the account
     * @param name the account name
     * @param openingBalance the initial account balance
     */
    public FinancialAccount(UUID id, UUID companyId, String name, Money openingBalance) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.balance = openingBalance;
        this.active = true;
    }

    /**
     * Increases the account balance by the given amount.
     *
     * @param amount the amount to credit
     */
    public void credit(Money amount) {
        this.balance = balance.add(amount);
    }

    /**
     * Decreases the account balance by the given amount.
     *
     * @param amount the amount to debit
     * @throws BusinessRuleViolationException if the resulting balance would be negative
     */
    public void debit(Money amount) {
        var updated = balance.subtract(amount);
        if (updated.isNegative()) {
            throw new BusinessRuleViolationException(
                    "Account %s does not have enough balance for this operation".formatted(name));
        }
        this.balance = updated;
    }

    /**
     * Renames the account.
     *
     * @param name the new account name
     */
    public void rename(String name) {
        this.name = name;
    }

    /**
     * Marks the account as inactive.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Returns the financial account identifier.
     *
     * @return the financial account identifier
     */
    public UUID getId() {
        return id;
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
    public Money getBalance() {
        return balance;
    }

    /**
     * Returns whether the account is currently active.
     *
     * @return {@code true} if the account is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }
}
