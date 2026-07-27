package br.com.nischor.ledgerxbackend.identity.domain.repository;

import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link User} aggregates, implemented by the infrastructure layer.
 */
public interface UserRepository {

    /**
     * Persists a user, inserting or updating as needed.
     *
     * @param user the user to save.
     * @return the persisted user.
     */
    User save(User user);

    /**
     * Looks up a user by identifier.
     *
     * @param id the user's identifier.
     * @return the matching user, or empty if none exists.
     */
    Optional<User> findById(UUID id);

    /**
     * Looks up a user by email address.
     *
     * @param email the user's email address.
     * @return the matching user, or empty if none exists.
     */
    Optional<User> findByEmail(EmailAddress email);

    /**
     * Checks whether a user with the given email address is already registered.
     *
     * @param email the email address to check.
     * @return {@code true} if a user with that email exists, {@code false} otherwise.
     */
    boolean existsByEmail(EmailAddress email);
}
