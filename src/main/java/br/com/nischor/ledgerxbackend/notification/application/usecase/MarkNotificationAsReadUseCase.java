package br.com.nischor.ledgerxbackend.notification.application.usecase;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.application.mapper.NotificationMapper;
import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Marks an existing {@link Notification} as read.
 */
@Service
public class MarkNotificationAsReadUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    /**
     * Creates the use case.
     *
     * @param notificationRepository repository used to load and persist notifications.
     * @param notificationMapper     mapper used to convert the updated notification to a DTO.
     */
    public MarkNotificationAsReadUseCase(NotificationRepository notificationRepository,
            NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Loads the notification, marks it as read and persists the change.
     *
     * @param notificationId identifier of the notification to mark as read.
     * @return the updated notification as a {@link NotificationDto}.
     * @throws EntityNotFoundException if no notification exists with the given identifier.
     */
    public NotificationDto execute(UUID notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(Notification.class, notificationId));
        notification.markAsRead();
        return notificationMapper.toDto(notificationRepository.save(notification));
    }
}
