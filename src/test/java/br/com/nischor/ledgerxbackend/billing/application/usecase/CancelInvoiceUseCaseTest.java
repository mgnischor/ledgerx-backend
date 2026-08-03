package br.com.nischor.ledgerxbackend.billing.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelInvoiceUseCaseTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    private CancelInvoiceUseCase useCase;

    private final UUID invoiceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CancelInvoiceUseCase(invoiceRepository, invoiceMapper);
    }

    private Invoice openInvoice() {
        var installment = new Installment(UUID.randomUUID(), 1, Money.brl(new BigDecimal("100.00")),
                LocalDate.now().plusDays(30));
        return new Invoice(invoiceId, UUID.randomUUID(), UUID.randomUUID(), PartyType.CUSTOMER, List.of(installment));
    }

    @Test
    void cancelsOpenInvoice() {
        var invoice = openInvoice();
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        var dto = new InvoiceDto(invoiceId, invoice.getCompanyId(), invoice.getPartyId(), PartyType.CUSTOMER,
                InvoiceStatus.CANCELED, 1);
        when(invoiceMapper.toDto(invoice)).thenReturn(dto);

        var result = useCase.execute(invoiceId);

        assertThat(result.status()).isEqualTo(InvoiceStatus.CANCELED);
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.CANCELED);
    }

    @Test
    void rejectsCancelingAFullyPaidInvoice() {
        var invoice = openInvoice();
        invoice.registerPayment(invoice.getInstallments().get(0).getId(), LocalDate.now());
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThatThrownBy(() -> useCase.execute(invoiceId)).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void rejectsUnknownInvoice() {
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(invoiceId)).isInstanceOf(EntityNotFoundException.class);
    }
}
