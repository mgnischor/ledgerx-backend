package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

/** Thrown when a JWT is malformed, has an invalid signature, or has expired. */
public class InvalidJwtException extends RuntimeException {

    /**
     * Creates a new exception with the given message.
     *
     * @param message a description of why the JWT was rejected
     */
    public InvalidJwtException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message a description of why the JWT was rejected
     * @param cause   the underlying cause of the failure
     */
    public InvalidJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
