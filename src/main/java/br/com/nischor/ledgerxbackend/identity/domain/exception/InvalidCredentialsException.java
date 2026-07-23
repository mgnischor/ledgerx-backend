package br.com.nischor.ledgerxbackend.identity.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
