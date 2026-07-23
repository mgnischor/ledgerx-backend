package br.com.nischor.ledgerxbackend.notification.application.dto;

import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import java.time.Instant;
import java.util.UUID;

public record NotificationDto(UUID id, NotificationType type, UUID referenceId, String message, Instant createdAt,
        boolean read) {
}
