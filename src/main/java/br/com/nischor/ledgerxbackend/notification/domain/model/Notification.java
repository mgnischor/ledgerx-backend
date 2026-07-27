package br.com.nischor.ledgerxbackend.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for an in-app notification generated from a domain event consumed by a
 * bounded context's message listener.
 */
public class Notification {

    private final UUID id;
    private final NotificationType type;
    /** Identifier of the entity the notification refers to. */
    private final UUID referenceId;
    private final String message;
    private final Instant createdAt;
    /** Whether the notification has already been read. */
    private boolean read;

    /**
     * Reconstructs a notification with all its attributes, typically used when rehydrating
     * from persistence.
     *
     * @param id          unique identifier of the notification.
     * @param type        category of the domain event that generated the notification.
     * @param referenceId identifier of the entity the notification refers to.
     * @param message     human-readable notification text.
     * @param createdAt   instant at which the notification was created.
     * @param read        whether the notification has already been read.
     */
    public Notification(UUID id, NotificationType type, UUID referenceId, String message, Instant createdAt,
            boolean read) {
        this.id = id;
        this.type = type;
        this.referenceId = referenceId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    /**
     * Creates a new, unread notification with the creation instant set to now.
     *
     * @param id          unique identifier of the notification.
     * @param type        category of the domain event that generated the notification.
     * @param referenceId identifier of the entity the notification refers to.
     * @param message     human-readable notification text.
     */
    public Notification(UUID id, NotificationType type, UUID referenceId, String message) {
        this(id, type, referenceId, message, Instant.now(), false);
    }

    /**
     * Marks this notification as read.
     */
    public void markAsRead() {
        this.read = true;
    }

    /**
     * Returns the notification's unique identifier.
     *
     * @return the notification id.
     */
    public UUID getId() {
        return id;
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
