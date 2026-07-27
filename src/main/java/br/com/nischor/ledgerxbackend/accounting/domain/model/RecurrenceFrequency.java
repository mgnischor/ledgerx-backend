package br.com.nischor.ledgerxbackend.accounting.domain.model;

/**
 * Frequency at which a {@link RecurringTransactionRule} generates a new transaction.
 */
public enum RecurrenceFrequency {
    /** Recurs every week. */
    WEEKLY,
    /** Recurs every month. */
    MONTHLY,
    /** Recurs every year. */
    YEARLY
}
