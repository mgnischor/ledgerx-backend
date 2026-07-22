package br.com.nischor.ledgerxbackend.billing.domain.model;

public enum InvoiceStatus {
    OPEN,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELED
}
