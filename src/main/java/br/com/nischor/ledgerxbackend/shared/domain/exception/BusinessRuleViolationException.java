package br.com.nischor.ledgerxbackend.shared.domain.exception;

/**
 * Thrown when an operation violates a business rule enforced by the domain
 * layer (e.g. an invariant that must hold for an aggregate or value object).
 */
public class BusinessRuleViolationException extends DomainException {

    /**
     * Creates a new exception describing the violated business rule.
     *
     * @param message a human-readable description of the violated rule
     */
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
