package br.com.nischor.ledgerxbackend.identity.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

public class EmailAlreadyRegisteredException extends BusinessRuleViolationException {

    public EmailAlreadyRegisteredException(String email) {
        super("Email already registered: %s".formatted(email));
    }
}
