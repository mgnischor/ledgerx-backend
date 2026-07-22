package br.com.nischor.ledgerxbackend.billing.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

public class InstallmentNotFoundException extends BusinessRuleViolationException {

    public InstallmentNotFoundException(String installmentId) {
        super("Installment not found: %s".formatted(installmentId));
    }
}
