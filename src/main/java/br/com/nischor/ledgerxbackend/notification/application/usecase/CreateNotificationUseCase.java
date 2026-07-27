package br.com.nischor.ledgerxbackend.notification.application.usecase;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Called by each bounded context's RabbitMQ message listener to persist a {@link Notification}
 * for a domain event it just consumed (BR-115), decoupling the notification feed from any single
 * context's domain model.
 */
@Service
public class CreateNotificationUseCase {

    private final NotificationRepository notificationRepository;

    /**
     * Creates the use case.
     *
     * @param notificationRepository repository used to persist created notifications.
     */
    public CreateNotificationUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates and persists a new notification.
     *
     * @param type        category of the domain event the notification is about.
     * @param referenceId identifier of the entity the notification refers to.
     * @param message     human-readable notification text; must not be blank.
     * @throws BusinessRuleViolationException if {@code message} is {@code null} or blank.
     */
    public void execute(NotificationType type, UUID referenceId, String message) {
        if (message == null || message.isBlank()) {
            throw new BusinessRuleViolationException("Notification message must not be blank");
        }

        notificationRepository.save(new Notification(UUID.randomUUID(), type, referenceId, message));
    }
}
