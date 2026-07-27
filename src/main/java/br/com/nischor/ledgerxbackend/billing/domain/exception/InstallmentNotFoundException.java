package br.com.nischor.ledgerxbackend.billing.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

/**
 * Thrown when an installment cannot be found by its identifier.
 */
public class InstallmentNotFoundException extends BusinessRuleViolationException {

    /**
     * Creates the exception with a message referencing the missing installment.
     *
     * @param installmentId the identifier of the installment that could not be found.
     */
    public InstallmentNotFoundException(String installmentId) {
        super("Installment not found: %s".formatted(installmentId));
    }
}
