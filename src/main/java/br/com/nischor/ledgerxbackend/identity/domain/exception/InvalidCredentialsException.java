package br.com.nischor.ledgerxbackend.identity.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.DomainException;

/**
 * Thrown when a login attempt fails because the supplied email or password does not match an
 * active user account.
 */
public class InvalidCredentialsException extends DomainException {

    /**
     * Creates the exception with a generic message that does not disclose whether the email or
     * the password was the cause of the failure.
     */
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
