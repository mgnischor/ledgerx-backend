package br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JPA persistence model for a user account, mapped to the {@code users} table (with roles stored
 * in the companion {@code user_roles} table). Converted to/from the {@code User} domain model by
 * {@code UserJpaMapper}.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    /** The already-hashed password; never the plain-text credential. */
    @Column(nullable = false)
    private String hashedPassword;

    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @jakarta.persistence.JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    /** Whether the account may currently authenticate. */
    @Column(nullable = false)
    private boolean active = true;

    /** JPA-required no-args constructor. */
    protected UserJpaEntity() {
    }

    /**
     * Creates a new entity with the given attributes. Roles default to empty and the account
     * defaults to active.
     *
     * @param id             the entity's unique identifier.
     * @param fullName       the user's full name.
     * @param email          the user's email address.
     * @param hashedPassword the already-hashed password to store.
     */
    public UserJpaEntity(UUID id, String fullName, String email, String hashedPassword) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.hashedPassword = hashedPassword;
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
    public String getEmail() {
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
     * Returns the mutable set of roles granted to this user.
     *
     * @return the granted roles.
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Returns whether this user account is active.
     *
     * @return {@code true} if active, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether this user account is active.
     *
     * @param active {@code true} to mark the account active, {@code false} to deactivate it.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
