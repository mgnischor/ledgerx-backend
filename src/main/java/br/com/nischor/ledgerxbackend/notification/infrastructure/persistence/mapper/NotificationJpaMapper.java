package br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link Notification} domain model and its
 * {@link NotificationJpaEntity} JPA persistence representation.
 */
@Component
public class NotificationJpaMapper {

    /**
     * Converts a JPA entity into its domain model representation.
     *
     * @param entity the JPA entity to convert.
     * @return the corresponding {@link Notification}.
     */
    public Notification toDomain(NotificationJpaEntity entity) {
        return new Notification(entity.getId(), entity.getType(), entity.getReferenceId(), entity.getMessage(),
                entity.getCreatedAt(), entity.isRead());
    }

    /**
     * Converts a domain notification into its JPA entity representation.
     *
     * @param notification the domain notification to convert.
     * @return the corresponding {@link NotificationJpaEntity}.
     */
    public NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(notification.getId(), notification.getType(),
                notification.getReferenceId(), notification.getMessage(), notification.getCreatedAt(),
                notification.isRead());
    }
}
