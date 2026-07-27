package br.com.nischor.ledgerxbackend.identity.domain.event;

import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a new user completes registration.
 *
 * @param userId     the identifier of the newly registered user.
 * @param email      the email address the user registered with.
 * @param occurredOn the instant at which the registration occurred.
 */
public record UserRegisteredEvent(UUID userId, String email, Instant occurredOn) implements DomainEvent {

    /**
     * Creates the event with the occurrence timestamp set to the current instant.
     *
     * @param userId the identifier of the newly registered user.
     * @param email  the email address the user registered with.
     */
    public UserRegisteredEvent(UUID userId, String email) {
        this(userId, email, Instant.now());
    }
}
