package br.com.nischor.ledgerxbackend.identity.infrastructure.messaging;

import br.com.nischor.ledgerxbackend.identity.domain.event.UserRegisteredEvent;
import br.com.nischor.ledgerxbackend.shared.infrastructure.messaging.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code ledgerx.events.user-registered}, proving the queue is wired end-to-end. Replace
 * the log line with real logic (e.g. sending a welcome email) when that becomes a requirement.
 */
@Component
public class UserRegisteredMessageListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredMessageListener.class);

    @RabbitListener(queues = RabbitMqConfig.QUEUE_USER_REGISTERED)
    public void onMessage(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: user {} ({})", event.userId(), event.email());
    }
}
