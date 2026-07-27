package br.com.nischor.ledgerxbackend.identity.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.identity.domain.event.UserRegisteredEvent;
import br.com.nischor.ledgerxbackend.notification.application.usecase.CreateNotificationUseCase;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code ledgerx.events.user-registered} and turns it into a persisted
 * {@link br.com.nischor.ledgerxbackend.notification.domain.model.Notification}, proving the queue
 * is wired end-to-end.
 */
@Component
public class UserRegisteredMessageListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredMessageListener.class);

    private final CreateNotificationUseCase createNotificationUseCase;

    /**
     * Creates the listener.
     *
     * @param createNotificationUseCase the use case used to persist the resulting notification.
     */
    public UserRegisteredMessageListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    /**
     * Handles an incoming {@link UserRegisteredEvent} message by creating a notification.
     *
     * @param event the deserialized message payload.
     */
    @RabbitListener(queues = RabbitMqConfig.QUEUE_USER_REGISTERED)
    public void onMessage(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: user {} ({})", event.userId(), event.email());
        createNotificationUseCase.execute(NotificationType.USER_REGISTERED, event.userId(),
                "New user registered: %s".formatted(event.email()));
    }
}
