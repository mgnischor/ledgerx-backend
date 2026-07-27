package br.com.nischor.ledgerxbackend.billing.application.usecase;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.event.InvoicePaidEvent;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Application use case that registers the payment of an invoice installment and publishes an
 * {@link InvoicePaidEvent} once the invoice becomes fully paid.
 */
@Service
public class RegisterPaymentUseCase {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final DomainEventPublisher eventPublisher;

    /**
     * Creates the use case.
     *
     * @param invoiceRepository repository used to load and persist invoices.
     * @param invoiceMapper     mapper used to convert the invoice to its DTO representation.
     * @param eventPublisher    publisher used to broadcast domain events, such as {@link InvoicePaidEvent}.
     */
    public RegisterPaymentUseCase(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper,
            DomainEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers the payment of one installment of an invoice. If, after the payment, all installments of the
     * invoice are paid, an {@link InvoicePaidEvent} is published.
     *
     * @param invoiceId     the identifier of the invoice.
     * @param installmentId the identifier of the installment being paid.
     * @param paidOn        the date the payment was made.
     * @return the DTO of the invoice after registering the payment.
     * @throws EntityNotFoundException if no invoice exists with the given identifier.
     * @throws br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException
     *         if the invoice is canceled or the installment does not belong to it.
     */
    public InvoiceDto execute(UUID invoiceId, UUID installmentId, LocalDate paidOn) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(Invoice.class, invoiceId));

        invoice.registerPayment(installmentId, paidOn);
        var saved = invoiceRepository.save(invoice);

        if (saved.getStatus() == InvoiceStatus.PAID) {
            eventPublisher.publish(new InvoicePaidEvent(saved.getId(), saved.getPartyId()));
        }

        return invoiceMapper.toDto(saved);
    }
}
