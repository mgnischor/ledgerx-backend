package br.com.nischor.ledgerxbackend.billing.application.mapper;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(Invoice invoice) {
        return new InvoiceDto(invoice.getId(), invoice.getCompanyId(), invoice.getPartyId(), invoice.getDirection(),
                invoice.getStatus(), invoice.getInstallments().size());
    }
}
