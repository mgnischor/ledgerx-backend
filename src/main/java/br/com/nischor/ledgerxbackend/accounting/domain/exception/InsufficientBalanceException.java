package br.com.nischor.ledgerxbackend.accounting.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

public class InsufficientBalanceException extends BusinessRuleViolationException {

    public InsufficientBalanceException(String accountName) {
        super("Account '%s' has insufficient balance for this operation".formatted(accountName));
    }
}
