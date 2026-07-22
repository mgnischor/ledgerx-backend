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

@Service
public class RegisterPaymentUseCase {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final DomainEventPublisher eventPublisher;

    public RegisterPaymentUseCase(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper,
            DomainEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.eventPublisher = eventPublisher;
    }

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
