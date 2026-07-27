package br.com.nischor.ledgerxbackend.accounting.domain.model;

/**
 * Classifies the nature of a financial transaction or category.
 */
public enum TransactionType {
    /** Money coming into an account. */
    INCOME,
    /** Money going out of an account. */
    EXPENSE,
    /** Money moved between two accounts. */
    TRANSFER
}
