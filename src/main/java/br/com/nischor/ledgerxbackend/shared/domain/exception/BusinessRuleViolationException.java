package br.com.nischor.ledgerxbackend.shared.domain.exception;

public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
