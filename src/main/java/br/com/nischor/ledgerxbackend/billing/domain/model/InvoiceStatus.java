package br.com.nischor.ledgerxbackend.billing.domain.model;

/**
 * Represents the lifecycle status of an {@link Invoice}.
 */
public enum InvoiceStatus {
    /** The invoice has been issued and no installment has been paid yet. */
    OPEN,
    /** At least one installment has been paid, but not all of them. */
    PARTIALLY_PAID,
    /** All installments have been paid. */
    PAID,
    /** The invoice is still open but at least one installment is past its due date. */
    OVERDUE,
    /** The invoice has been canceled and no further payments can be registered. */
    CANCELED
}
