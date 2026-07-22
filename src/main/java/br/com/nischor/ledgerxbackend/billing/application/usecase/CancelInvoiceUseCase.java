package br.com.nischor.ledgerxbackend.billing.application.usecase;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CancelInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public CancelInvoiceUseCase(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    public InvoiceDto execute(UUID invoiceId) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException(Invoice.class, invoiceId));
        invoice.cancel();
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }
}
