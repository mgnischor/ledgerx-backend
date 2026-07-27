package br.com.nischor.ledgerxbackend.billing.domain.service;

import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import java.time.LocalDate;
import java.util.List;

/**
 * Domain service that evaluates a batch of invoices and transitions the overdue ones to
 * {@link br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus#OVERDUE}.
 */
public class OverdueInvoiceService {

    /**
     * Marks each invoice in the given list as overdue if it is open and has at least one overdue installment as of
     * the reference date.
     *
     * @param invoices      the invoices to evaluate.
     * @param referenceDate the date used to evaluate whether installments are overdue.
     */
    public void markOverdueInvoices(List<Invoice> invoices, LocalDate referenceDate) {
        invoices.forEach(invoice -> invoice.markOverdueIfNeeded(referenceDate));
    }
}
