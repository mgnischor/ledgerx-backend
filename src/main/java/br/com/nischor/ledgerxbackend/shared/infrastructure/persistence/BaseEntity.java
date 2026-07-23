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

    protected BaseEntity() {
    }

    protected BaseEntity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

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

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
