package br.com.nischor.ledgerxbackend.notification.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateNotificationUseCaseTest {

    @Mock
    private NotificationRepository notificationRepository;

    private CreateNotificationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateNotificationUseCase(notificationRepository);
    }

    @Test
    void createsAndPersistsNotification() {
        useCase.execute(NotificationType.INVOICE_PAID, UUID.randomUUID(), "Invoice paid");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void rejectsBlankMessage() {
        assertThatThrownBy(() -> useCase.execute(NotificationType.INVOICE_PAID, UUID.randomUUID(), "   "))
                .isInstanceOf(BusinessRuleViolationException.class);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void rejectsNullMessage() {
        assertThatThrownBy(() -> useCase.execute(NotificationType.INVOICE_PAID, UUID.randomUUID(), null))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
