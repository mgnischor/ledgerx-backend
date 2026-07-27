package br.com.nischor.ledgerxbackend.notification.domain.model;

/**
 * Categories of domain events that can trigger the creation of a {@link Notification}.
 */
public enum NotificationType {
    /** A new user has registered. */
    USER_REGISTERED,
    /** A transaction has been recorded. */
    TRANSACTION_RECORDED,
    /** An invoice has been paid. */
    INVOICE_PAID
}
