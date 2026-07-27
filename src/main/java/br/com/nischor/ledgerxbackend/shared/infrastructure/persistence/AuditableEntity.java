package br.com.nischor.ledgerxbackend.shared.infrastructure.persistence;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base class for JPA entities that require automatic creation/last-modification
 * timestamp auditing, powered by Spring Data's {@link AuditingEntityListener}.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity extends BaseEntity {

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Creates a new auditable entity with no identifier set (for JPA/framework use).
     */
    protected AuditableEntity() {
    }

    /**
     * Creates a new auditable entity with the given identifier.
     *
     * @param id the entity identifier, assigned by the domain layer
     */
    protected AuditableEntity(UUID id) {
        super(id);
    }

    /**
     * Returns the instant at which this entity was first persisted.
     *
     * @return the creation timestamp, populated automatically on insert
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the instant at which this entity was last modified.
     *
     * @return the last-update timestamp, populated automatically on update
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
