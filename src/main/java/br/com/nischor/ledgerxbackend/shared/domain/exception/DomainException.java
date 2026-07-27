package br.com.nischor.ledgerxbackend.shared.domain.exception;

/**
 * Base type for all exceptions representing violations of domain rules or
 * domain-level error conditions.
 *
 * <p>Being a runtime exception, it does not force callers to handle it
 * explicitly, but allows infrastructure layers (e.g. web exception handlers)
 * to translate it into an appropriate response.
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Creates a new domain exception with the given message.
     *
     * @param message the detail message describing the domain error
     */
    protected DomainException(String message) {
        super(message);
    }

    /**
     * Creates a new domain exception with the given message and cause.
     *
     * @param message the detail message describing the domain error
     * @param cause   the underlying cause of this exception
     */
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
