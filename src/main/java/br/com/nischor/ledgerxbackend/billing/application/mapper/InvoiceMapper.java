package br.com.nischor.ledgerxbackend.billing.application.mapper;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Invoice} domain objects into {@link InvoiceDto} instances for use in the application layer.
 */
@Component
public class InvoiceMapper {

    /**
     * Maps an {@link Invoice} to its {@link InvoiceDto} representation.
     *
     * @param invoice the invoice to convert.
     * @return the resulting DTO.
     */
    public InvoiceDto toDto(Invoice invoice) {
        return new InvoiceDto(invoice.getId(), invoice.getCompanyId(), invoice.getPartyId(), invoice.getDirection(),
                invoice.getStatus(), invoice.getInstallments().size());
    }
}
