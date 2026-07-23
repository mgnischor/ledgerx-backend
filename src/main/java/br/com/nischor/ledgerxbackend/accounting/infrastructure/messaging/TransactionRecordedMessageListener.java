package br.com.nischor.ledgerxbackend.accounting.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.accounting.domain.event.TransactionRecordedEvent;
import br.com.nischor.ledgerxbackend.notification.application.usecase.CreateNotificationUseCase;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code ledgerx.events.transaction-recorded} and turns it into a persisted
 * {@link br.com.nischor.ledgerxbackend.notification.domain.model.Notification}, proving the queue
 * is wired end-to-end.
 */
@Component
public class TransactionRecordedMessageListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionRecordedMessageListener.class);

    private final CreateNotificationUseCase createNotificationUseCase;

    public TransactionRecordedMessageListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_TRANSACTION_RECORDED)
    public void onMessage(TransactionRecordedEvent event) {
        log.info("Received TransactionRecordedEvent: transaction {} on account {} ({} {})",
                event.transactionId(), event.financialAccountId(), event.type(), event.amount());
        createNotificationUseCase.execute(NotificationType.TRANSACTION_RECORDED, event.transactionId(),
                "%s of %s recorded on account %s".formatted(event.type(), event.amount(),
                        event.financialAccountId()));
    }
}
