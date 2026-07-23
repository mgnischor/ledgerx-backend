package br.com.nischor.ledgerxbackend.notification.application.usecase;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.application.mapper.NotificationMapper;
import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MarkNotificationAsReadUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public MarkNotificationAsReadUseCase(NotificationRepository notificationRepository,
            NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    public NotificationDto execute(UUID notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(Notification.class, notificationId));
        notification.markAsRead();
        return notificationMapper.toDto(notificationRepository.save(notification));
    }
}
