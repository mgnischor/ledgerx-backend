package br.com.nischor.ledgerxbackend.notification.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.notification.domain.model.Notification;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var notification = new Notification(UUID.randomUUID(), NotificationType.INVOICE_PAID, UUID.randomUUID(),
                "Invoice paid");

        var dto = mapper.toDto(notification);

        assertThat(dto.id()).isEqualTo(notification.getId());
        assertThat(dto.type()).isEqualTo(NotificationType.INVOICE_PAID);
        assertThat(dto.referenceId()).isEqualTo(notification.getReferenceId());
        assertThat(dto.message()).isEqualTo("Invoice paid");
        assertThat(dto.createdAt()).isEqualTo(notification.getCreatedAt());
        assertThat(dto.read()).isFalse();
    }
}
