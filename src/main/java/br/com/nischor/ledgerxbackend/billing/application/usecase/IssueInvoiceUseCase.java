package br.com.nischor.ledgerxbackend.billing.application.usecase;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IssueInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final PartyRepository partyRepository;
    private final InvoiceMapper invoiceMapper;

    public IssueInvoiceUseCase(InvoiceRepository invoiceRepository, PartyRepository partyRepository,
            InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.partyRepository = partyRepository;
        this.invoiceMapper = invoiceMapper;
    }

    public InvoiceDto execute(UUID companyId, UUID partyId, PartyType direction, List<Money> installmentAmounts,
            LocalDate firstDueDate) {
        partyRepository.findById(partyId).orElseThrow(() -> new EntityNotFoundException(Party.class, partyId));

        for (var amount : installmentAmounts) {
            if (!amount.isPositive()) {
                throw new BusinessRuleViolationException("Every installment amount must be strictly positive");
            }
        }

        var installments = installmentAmounts.stream()
                .map(amount -> new Installment(UUID.randomUUID(), installmentAmounts.indexOf(amount) + 1, amount,
                        firstDueDate.plusMonths(installmentAmounts.indexOf(amount))))
                .toList();

        var invoice = new Invoice(UUID.randomUUID(), companyId, partyId, direction, installments);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }
}
