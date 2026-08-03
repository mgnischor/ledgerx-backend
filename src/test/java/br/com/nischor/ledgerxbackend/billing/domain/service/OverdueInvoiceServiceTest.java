package br.com.nischor.ledgerxbackend.billing.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OverdueInvoiceServiceTest {

    private final OverdueInvoiceService service = new OverdueInvoiceService();

    private Invoice invoiceDueOn(LocalDate dueDate) {
        var installment = new Installment(UUID.randomUUID(), 1, Money.brl(new BigDecimal("100.00")), dueDate);
        return new Invoice(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), PartyType.CUSTOMER,
                List.of(installment));
    }

    @Test
    void marksOpenInvoiceWithOverdueInstallmentAsOverdue() {
        var invoice = invoiceDueOn(LocalDate.now().minusDays(1));

        service.markOverdueInvoices(List.of(invoice), LocalDate.now());

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.OVERDUE);
    }

    @Test
    void leavesInvoiceWithoutOverdueInstallmentsOpen() {
        var invoice = invoiceDueOn(LocalDate.now().plusDays(5));

        service.markOverdueInvoices(List.of(invoice), LocalDate.now());

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.OPEN);
    }

    @Test
    void doesNotMarkPaidInvoicesAsOverdue() {
        var invoice = invoiceDueOn(LocalDate.now().minusDays(1));
        invoice.registerPayment(invoice.getInstallments().get(0).getId(), LocalDate.now());

        service.markOverdueInvoices(List.of(invoice), LocalDate.now());

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
    }

    @Test
    void handlesEmptyInvoiceList() {
        service.markOverdueInvoices(List.of(), LocalDate.now());
    }
}
