package br.com.nischor.ledgerxbackend.billing.application.usecase;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Application use case that cancels an existing invoice.
 */
@Service
public class CancelInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    /**
     * Creates the use case.
     *
     * @param invoiceRepository repository used to load and persist invoices.
     * @param invoiceMapper     mapper used to convert the invoice to its DTO representation.
     */
    public CancelInvoiceUseCase(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    /**
     * Cancels the invoice identified by {@code invoiceId}.
     *
     * @param invoiceId the identifier of the invoice to cancel.
     * @return the DTO of the invoice after cancellation.
     * @throws EntityNotFoundException if no invoice exists with the given identifier.
     * @throws br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException
     *         if the invoice is already fully paid and therefore cannot be canceled.
     */
    public InvoiceDto execute(UUID invoiceId) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(Invoice.class, invoiceId));
        invoice.cancel();
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }
}
