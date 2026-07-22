package br.com.nischor.ledgerxbackend.billing.application.usecase;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IssueInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public IssueInvoiceUseCase(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    public InvoiceDto execute(UUID companyId, UUID partyId, PartyType direction, List<Money> installmentAmounts,
            LocalDate firstDueDate) {
        var installments = installmentAmounts.stream()
                .map(amount -> new Installment(UUID.randomUUID(), installmentAmounts.indexOf(amount) + 1, amount,
                        firstDueDate.plusMonths(installmentAmounts.indexOf(amount))))
                .toList();

        var invoice = new Invoice(UUID.randomUUID(), companyId, partyId, direction, installments);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }
}
