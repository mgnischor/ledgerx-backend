package br.com.nischor.ledgerxbackend.accounting.domain.service;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;

/**
 * Domain service that applies the balance effect of a transaction to a financial account based on its type.
 */
public class AccountBalanceService {

    /**
     * Applies the given transaction type and amount to the account balance, crediting it for income and
     * debiting it for expenses.
     *
     * @param account the financial account to update
     * @param type the transaction type (income or expense) determining the balance effect
     * @param amount the amount to apply
     * @throws br.com.nischor.ledgerxbackend.accounting.domain.exception.InsufficientBalanceException if an
     *         expense would leave the account with a negative balance (thrown transitively by
     *         {@link FinancialAccount#debit(Money)})
     * @throws UnsupportedOperationException if the transaction type is {@code TRANSFER}, since transfers must
     *         be applied on both the source and destination accounts separately
     */
    public void apply(FinancialAccount account, TransactionType type, Money amount) {
        switch (type) {
            case INCOME -> account.credit(amount);
            case EXPENSE -> account.debit(amount);
            case TRANSFER -> throw new UnsupportedOperationException(
                    "Transfers must be applied on both the source and destination accounts");
        }
    }
}
