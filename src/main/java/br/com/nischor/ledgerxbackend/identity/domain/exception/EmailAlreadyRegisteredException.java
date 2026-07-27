package br.com.nischor.ledgerxbackend.identity.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

/**
 * Thrown when an attempt is made to register a user with an email address that is already in use.
 */
public class EmailAlreadyRegisteredException extends BusinessRuleViolationException {

    /**
     * Creates the exception for the given email address.
     *
     * @param email the email address that is already registered.
     */
    public EmailAlreadyRegisteredException(String email) {
        super("Email already registered: %s".formatted(email));
    }
}
