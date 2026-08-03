package br.com.nischor.ledgerxbackend.billing.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.event.InvoicePaidEvent;
import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
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
class RegisterPaymentUseCaseTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    private RegisterPaymentUseCase useCase;

    private final UUID invoiceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new RegisterPaymentUseCase(invoiceRepository, invoiceMapper, eventPublisher);
    }

    @Test
    void publishesInvoicePaidEventWhenLastInstallmentIsSettled() {
        var installment = new Installment(UUID.randomUUID(), 1, Money.brl(new BigDecimal("100.00")),
                LocalDate.now().plusDays(5));
        var invoice = new Invoice(invoiceId, UUID.randomUUID(), UUID.randomUUID(), PartyType.CUSTOMER,
                List.of(installment));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        var dto = new InvoiceDto(invoiceId, invoice.getCompanyId(), invoice.getPartyId(), PartyType.CUSTOMER,
                InvoiceStatus.PAID, 1);
        when(invoiceMapper.toDto(invoice)).thenReturn(dto);

        var result = useCase.execute(invoiceId, installment.getId(), LocalDate.now());

        assertThat(result.status()).isEqualTo(InvoiceStatus.PAID);
        verify(eventPublisher).publish(any(InvoicePaidEvent.class));
    }

    @Test
    void doesNotPublishEventWhenInvoiceIsOnlyPartiallyPaid() {
        var installment1 = new Installment(UUID.randomUUID(), 1, Money.brl(new BigDecimal("100.00")),
                LocalDate.now().plusDays(5));
        var installment2 = new Installment(UUID.randomUUID(), 2, Money.brl(new BigDecimal("100.00")),
                LocalDate.now().plusDays(35));
        var invoice = new Invoice(invoiceId, UUID.randomUUID(), UUID.randomUUID(), PartyType.CUSTOMER,
                List.of(installment1, installment2));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        var dto = new InvoiceDto(invoiceId, invoice.getCompanyId(), invoice.getPartyId(), PartyType.CUSTOMER,
                InvoiceStatus.PARTIALLY_PAID, 2);
        when(invoiceMapper.toDto(invoice)).thenReturn(dto);

        var result = useCase.execute(invoiceId, installment1.getId(), LocalDate.now());

        assertThat(result.status()).isEqualTo(InvoiceStatus.PARTIALLY_PAID);
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void rejectsUnknownInvoice() {
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(invoiceId, UUID.randomUUID(), LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
