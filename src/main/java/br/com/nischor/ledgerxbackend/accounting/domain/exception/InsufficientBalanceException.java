package br.com.nischor.ledgerxbackend.accounting.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

/**
 * Thrown when an operation would require a financial account to hold a balance greater than it currently has.
 */
public class InsufficientBalanceException extends BusinessRuleViolationException {

    /**
     * Creates the exception with a message identifying the affected account.
     *
     * @param accountName the name of the account that has insufficient balance
     */
    public InsufficientBalanceException(String accountName) {
        super("Account '%s' has insufficient balance for this operation".formatted(accountName));
    }
}
