package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationJpaMapper {

    public Notification toDomain(NotificationJpaEntity entity) {
        return new Notification(entity.getId(), entity.getType(), entity.getReferenceId(), entity.getMessage(),
                entity.getCreatedAt(), entity.isRead());
    }

    public NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(notification.getId(), notification.getType(),
                notification.getReferenceId(), notification.getMessage(), notification.getCreatedAt(),
                notification.isRead());
    }
}
