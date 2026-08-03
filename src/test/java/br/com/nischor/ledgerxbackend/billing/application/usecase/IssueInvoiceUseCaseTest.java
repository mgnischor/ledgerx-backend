package br.com.nischor.ledgerxbackend.billing.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
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
class IssueInvoiceUseCaseTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    private IssueInvoiceUseCase useCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID partyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new IssueInvoiceUseCase(invoiceRepository, partyRepository, invoiceMapper);
    }

    private Party party() {
        return new Party(partyId, companyId, "Jane Doe", DocumentNumber.cpf("11144477735"),
                new EmailAddress("jane@example.com"), PartyType.CUSTOMER);
    }

    @Test
    void issuesInvoiceWithOneInstallmentPerAmount() {
        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party()));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new InvoiceDto(UUID.randomUUID(), companyId, partyId, PartyType.CUSTOMER, InvoiceStatus.OPEN, 2);
        when(invoiceMapper.toDto(any(Invoice.class))).thenReturn(dto);

        var amounts = List.of(Money.brl(new BigDecimal("100.00")), Money.brl(new BigDecimal("100.00")));
        var result = useCase.execute(companyId, partyId, PartyType.CUSTOMER, amounts, LocalDate.now().plusDays(30));

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void rejectsUnknownParty() {
        when(partyRepository.findById(partyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(companyId, partyId, PartyType.CUSTOMER,
                List.of(Money.brl(new BigDecimal("100.00"))), LocalDate.now().plusDays(30)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsNonPositiveInstallmentAmount() {
        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party()));

        assertThatThrownBy(() -> useCase.execute(companyId, partyId, PartyType.CUSTOMER,
                List.of(Money.brl(new BigDecimal("0.00"))), LocalDate.now().plusDays(30)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
