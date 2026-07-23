package br.com.nischor.ledgerxbackend.shared.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Declares the AMQP topology backing domain event messaging: one durable topic exchange with one
 * durable queue per event type. Spring Boot's auto-configured {@code RabbitAdmin} declares every
 * {@link Queue}/{@link TopicExchange}/{@link Binding} bean found in the context against the broker
 * on startup, so no manual admin wiring is needed.
 */
@Configuration
public class RabbitMqConfig {

    public static final String EVENTS_EXCHANGE = "ledgerx.events";

    public static final String ROUTING_KEY_USER_REGISTERED = "identity.user.registered";
    public static final String ROUTING_KEY_TRANSACTION_RECORDED = "accounting.transaction.recorded";
    public static final String ROUTING_KEY_INVOICE_PAID = "billing.invoice.paid";

    public static final String QUEUE_USER_REGISTERED = "ledgerx.events.user-registered";
    public static final String QUEUE_TRANSACTION_RECORDED = "ledgerx.events.transaction-recorded";
    public static final String QUEUE_INVOICE_PAID = "ledgerx.events.invoice-paid";

    @Bean
    public TopicExchange domainEventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable(QUEUE_USER_REGISTERED).build();
    }

    @Bean
    public Queue transactionRecordedQueue() {
        return QueueBuilder.durable(QUEUE_TRANSACTION_RECORDED).build();
    }

    @Bean
    public Queue invoicePaidQueue() {
        return QueueBuilder.durable(QUEUE_INVOICE_PAID).build();
    }

    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, TopicExchange domainEventsExchange) {
        return BindingBuilder.bind(userRegisteredQueue).to(domainEventsExchange).with(ROUTING_KEY_USER_REGISTERED);
    }

    @Bean
    public Binding transactionRecordedBinding(Queue transactionRecordedQueue, TopicExchange domainEventsExchange) {
        return BindingBuilder.bind(transactionRecordedQueue).to(domainEventsExchange)
                .with(ROUTING_KEY_TRANSACTION_RECORDED);
    }

    @Bean
    public Binding invoicePaidBinding(Queue invoicePaidQueue, TopicExchange domainEventsExchange) {
        return BindingBuilder.bind(invoicePaidQueue).to(domainEventsExchange).with(ROUTING_KEY_INVOICE_PAID);
    }

    /**
     * Registered as the sole {@link MessageConverter} bean so Spring Boot's auto-configured
     * {@code RabbitTemplate} and {@code @RabbitListener} containers both pick it up automatically,
     * exchanging JSON instead of Java serialization. Spring Boot 4 / Spring Framework 7 default to
     * Jackson 3, so this takes the auto-configured {@link JsonMapper} rather than a classic
     * Jackson 2 {@code ObjectMapper}.
     */
    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
