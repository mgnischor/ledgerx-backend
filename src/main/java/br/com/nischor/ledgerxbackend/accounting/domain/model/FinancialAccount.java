package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;

public class FinancialAccount {

    private final UUID id;
    private final UUID companyId;
    private String name;
    private Money balance;
    private boolean active;

    public FinancialAccount(UUID id, UUID companyId, String name, Money openingBalance) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.balance = openingBalance;
        this.active = true;
    }

    public void credit(Money amount) {
        this.balance = balance.add(amount);
    }

    public void debit(Money amount) {
        var updated = balance.subtract(amount);
        if (updated.isNegative()) {
            throw new BusinessRuleViolationException(
                    "Account %s does not have enough balance for this operation".formatted(name));
        }
        this.balance = updated;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public Money getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
}
