package br.com.nischor.ledgerxbackend.notification.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.application.mapper.NotificationMapper;
import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarkNotificationAsReadUseCaseTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    private MarkNotificationAsReadUseCase useCase;

    private final UUID notificationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new MarkNotificationAsReadUseCase(notificationRepository, notificationMapper);
    }

    @Test
    void marksExistingNotificationAsRead() {
        var notification = new Notification(notificationId, NotificationType.INVOICE_PAID, UUID.randomUUID(),
                "Invoice paid", Instant.now(), false);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        var dto = new NotificationDto(notificationId, NotificationType.INVOICE_PAID, notification.getReferenceId(),
                "Invoice paid", notification.getCreatedAt(), true);
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        var result = useCase.execute(notificationId);

        assertThat(result.read()).isTrue();
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void rejectsUnknownNotification() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(notificationId)).isInstanceOf(EntityNotFoundException.class);
    }
}
