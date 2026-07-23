package br.com.nischor.ledgerxbackend.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final NotificationType type;
    private final UUID referenceId;
    private final String message;
    private final Instant createdAt;
    private boolean read;

    public Notification(UUID id, NotificationType type, UUID referenceId, String message, Instant createdAt,
            boolean read) {
        this.id = id;
        this.type = type;
        this.referenceId = referenceId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public Notification(UUID id, NotificationType type, UUID referenceId, String message) {
        this(id, type, referenceId, message, Instant.now(), false);
    }

    public void markAsRead() {
        this.read = true;
    }

    public UUID getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }
}
