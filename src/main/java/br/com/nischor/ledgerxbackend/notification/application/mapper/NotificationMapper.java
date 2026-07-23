package br.com.nischor.ledgerxbackend.notification.application.mapper;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getType(), notification.getReferenceId(),
                notification.getMessage(), notification.getCreatedAt(), notification.isRead());
    }
}
