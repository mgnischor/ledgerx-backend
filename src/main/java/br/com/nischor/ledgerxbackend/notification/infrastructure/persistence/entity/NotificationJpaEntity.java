package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity mapping for the {@code notifications} table, used to persist
 * {@link br.com.nischor.ledgerxbackend.notification.domain.model.Notification} domain objects.
 */
@Entity
@Table(name = "notifications")
public class NotificationJpaEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    /** Identifier of the entity the notification refers to. */
    @Column(nullable = false)
    private UUID referenceId;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    /** Whether the notification has already been read. */
    @Column(nullable = false)
    private boolean read;

    /**
     * Default constructor required by JPA.
     */
    protected NotificationJpaEntity() {
    }

    /**
     * Creates a new entity instance with all its attributes.
     *
     * @param id          unique identifier of the notification.
     * @param type        category of the domain event that generated the notification.
     * @param referenceId identifier of the entity the notification refers to.
     * @param message     human-readable notification text.
     * @param createdAt   instant at which the notification was created.
     * @param read        whether the notification has already been read.
     */
    public NotificationJpaEntity(UUID id, NotificationType type, UUID referenceId, String message,
            Instant createdAt, boolean read) {
        super(id);
        this.type = type;
        this.referenceId = referenceId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    /**
     * Returns the category of the domain event that generated this notification.
     *
     * @return the notification type.
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Returns the identifier of the entity this notification refers to.
     *
     * @return the reference id.
     */
    public UUID getReferenceId() {
        return referenceId;
    }

    /**
     * Returns the human-readable notification text.
     *
     * @return the notification message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the instant at which this notification was created.
     *
     * @return the creation instant.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Indicates whether this notification has already been read.
     *
     * @return {@code true} if the notification has been read, {@code false} otherwise.
     */
    public boolean isRead() {
        return read;
    }
}
