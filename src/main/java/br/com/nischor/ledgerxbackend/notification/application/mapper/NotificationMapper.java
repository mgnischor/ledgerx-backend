package br.com.nischor.ledgerxbackend.notification.application.mapper;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Notification} domain objects into their {@link NotificationDto} read-model
 * representation.
 */
@Component
public class NotificationMapper {

    /**
     * Converts a domain notification into its DTO representation.
     *
     * @param notification the domain notification to convert.
     * @return the corresponding {@link NotificationDto}.
     */
    public NotificationDto toDto(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getType(), notification.getReferenceId(),
                notification.getMessage(), notification.getCreatedAt(), notification.isRead());
    }
}
