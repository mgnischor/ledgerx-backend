package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

/** Thrown when a JWT is malformed, has an invalid signature, or has expired. */
public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(String message) {
        super(message);
    }

    public InvalidJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
