package br.com.nischor.ledgerxbackend.notification.application.dto;

import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import java.time.Instant;
import java.util.UUID;

/**
 * Read-model representation of a {@code Notification}, exposed to API clients.
 *
 * @param id          unique identifier of the notification.
 * @param type        category of the domain event that generated the notification.
 * @param referenceId identifier of the entity the notification refers to.
 * @param message     human-readable notification text.
 * @param createdAt   instant at which the notification was created.
 * @param read        whether the notification has already been read.
 */
public record NotificationDto(UUID id, NotificationType type, UUID referenceId, String message, Instant createdAt,
        boolean read) {
}
