package br.com.nischor.ledgerxbackend.accounting.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.accounting.domain.event.TransactionRecordedEvent;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code ledgerx.events.transaction-recorded}, proving the queue is wired end-to-end.
 * Replace the log line with real logic (e.g. updating a read-side cash-flow projection) when that
 * becomes a requirement.
 */
@Component
public class TransactionRecordedMessageListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionRecordedMessageListener.class);

    @RabbitListener(queues = RabbitMqConfig.QUEUE_TRANSACTION_RECORDED)
    public void onMessage(TransactionRecordedEvent event) {
        log.info("Received TransactionRecordedEvent: transaction {} on account {} ({} {})",
                event.transactionId(), event.financialAccountId(), event.type(), event.amount());
    }
}
