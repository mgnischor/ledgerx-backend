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

@Entity
@Table(name = "notifications")
public class NotificationJpaEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private UUID referenceId;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean read;

    protected NotificationJpaEntity() {
    }

    public NotificationJpaEntity(UUID id, NotificationType type, UUID referenceId, String message,
            Instant createdAt, boolean read) {
        super(id);
        this.type = type;
        this.referenceId = referenceId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
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
