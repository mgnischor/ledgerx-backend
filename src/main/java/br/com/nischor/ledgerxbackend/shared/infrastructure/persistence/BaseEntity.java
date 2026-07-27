package br.com.nischor.ledgerxbackend.shared.infrastructure.persistence;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate identifiers are always assigned by the domain layer ({@code UUID.randomUUID()})
 * before an entity is ever persisted, so {@code id} is never database-generated. Every mapper
 * must set it explicitly when converting a domain object to a JPA entity — otherwise
 * {@code JpaRepository.save()} on an already-persisted aggregate would insert a duplicate row
 * instead of updating the existing one.
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    private UUID id;

    /**
     * Creates a new entity with no identifier set (for JPA/framework use).
     */
    protected BaseEntity() {
    }

    /**
     * Creates a new entity with the given identifier.
     *
     * @param id the entity identifier, assigned by the domain layer
     */
    protected BaseEntity(UUID id) {
        this.id = id;
    }

    /**
     * Returns the identifier of this entity.
     *
     * @return the entity's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Compares entities by identity, based solely on their identifier.
     * Two entities with a {@code null} id are never considered equal.
     *
     * @param other the object to compare with
     * @return {@code true} if {@code other} is a {@code BaseEntity} with the same non-null id
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BaseEntity that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    /**
     * Computes a hash code consistent with {@link #equals(Object)}, based on the identifier.
     *
     * @return the hash code derived from the entity's id
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
