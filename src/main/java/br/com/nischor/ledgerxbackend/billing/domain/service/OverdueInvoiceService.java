package br.com.nischor.ledgerxbackend.billing.domain.service;

import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import java.time.LocalDate;
import java.util.List;

public class OverdueInvoiceService {

    public void markOverdueInvoices(List<Invoice> invoices, LocalDate referenceDate) {
        invoices.forEach(invoice -> invoice.markOverdueIfNeeded(referenceDate));
    }
}
