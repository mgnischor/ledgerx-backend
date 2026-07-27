package br.com.nischor.ledgerxbackend.billing.domain.model;

/**
 * Represents the role a {@link Party} plays in a transaction, and the direction of the associated invoice.
 */
public enum PartyType {
    /** The party is a customer; invoices of this type are issued to the party. */
    CUSTOMER,
    /** The party is a supplier; invoices of this type are received from the party. */
    SUPPLIER
}
