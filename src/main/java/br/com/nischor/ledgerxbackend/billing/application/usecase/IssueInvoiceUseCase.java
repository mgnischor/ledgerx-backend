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

/**
 * Application use case that issues a new invoice for a party, splitting the total amount into monthly installments.
 */
@Service
public class IssueInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final PartyRepository partyRepository;
    private final InvoiceMapper invoiceMapper;

    /**
     * Creates the use case.
     *
     * @param invoiceRepository repository used to persist invoices.
     * @param partyRepository   repository used to validate the existence of the invoice's party.
     * @param invoiceMapper     mapper used to convert the invoice to its DTO representation.
     */
    public IssueInvoiceUseCase(InvoiceRepository invoiceRepository, PartyRepository partyRepository,
            InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.partyRepository = partyRepository;
        this.invoiceMapper = invoiceMapper;
    }

    /**
     * Issues a new invoice with one installment per entry in {@code installmentAmounts}, each due one month apart
     * starting on {@code firstDueDate}.
     *
     * @param companyId           the identifier of the company issuing the invoice.
     * @param partyId             the identifier of the counterparty of the invoice.
     * @param direction           whether the invoice is issued to a customer or received from a supplier.
     * @param installmentAmounts  the amount of each installment, in order; every amount must be strictly positive.
     * @param firstDueDate        the due date of the first installment; subsequent installments are due one month
     *                            later per index.
     * @return the DTO of the newly issued invoice.
     * @throws EntityNotFoundException if no party exists with the given identifier.
     * @throws BusinessRuleViolationException if any installment amount is not strictly positive.
     */
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
