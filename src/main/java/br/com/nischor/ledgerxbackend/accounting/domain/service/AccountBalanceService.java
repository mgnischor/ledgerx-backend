package br.com.nischor.ledgerxbackend.accounting.domain.service;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;

public class AccountBalanceService {

    public void apply(FinancialAccount account, TransactionType type, Money amount) {
        switch (type) {
            case INCOME -> account.credit(amount);
            case EXPENSE -> account.debit(amount);
            case TRANSFER -> throw new UnsupportedOperationException(
                    "Transfers must be applied on both the source and destination accounts");
        }
    }
}
