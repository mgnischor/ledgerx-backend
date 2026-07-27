package br.com.nischor.ledgerxbackend.identity.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root representing a registered user account: identity, credentials, granted
 * {@link Role}s and activation state.
 */
public class User {

    private final UUID id;
    private String fullName;
    private EmailAddress email;
    /** The already-hashed password; never the plain-text credential. */
    private String hashedPassword;
    private final Set<Role> roles;
    /** Whether the account may currently authenticate. New users start active. */
    private boolean active;

    /**
     * Creates a new, active user with no granted roles.
     *
     * @param id             the user's unique identifier.
     * @param fullName       the user's full name.
     * @param email          the user's email address.
     * @param hashedPassword the already-hashed password to store.
     */
    public User(UUID id, String fullName, EmailAddress email, String hashedPassword) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.roles = EnumSet.noneOf(Role.class);
        this.active = true;
    }

    /**
     * Grants a role to this user.
     *
     * @param role the role to grant.
     */
    public void grant(Role role) {
        roles.add(role);
    }

    /**
     * Revokes a role from this user.
     *
     * @param role the role to revoke.
     */
    public void revoke(Role role) {
        roles.remove(role);
    }

    /**
     * Deactivates this user, preventing further authentication.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the user's full name.
     *
     * @return the full name.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address.
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Returns the user's hashed password.
     *
     * @return the hashed password.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Returns the roles currently granted to this user.
     *
     * @return an immutable copy of the granted roles.
     */
    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }

    /**
     * Returns whether this user account is active.
     *
     * @return {@code true} if the account may authenticate, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }
}
