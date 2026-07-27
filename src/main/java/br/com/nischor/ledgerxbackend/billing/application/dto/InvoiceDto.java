package br.com.nischor.ledgerxbackend.billing.application.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import java.util.UUID;

/**
 * Data transfer object exposing a read-only, application-layer view of an {@code Invoice}.
 *
 * @param id               the invoice identifier.
 * @param companyId        the identifier of the company the invoice belongs to.
 * @param partyId           the identifier of the counterparty (customer or supplier) of the invoice.
 * @param direction         whether the invoice is issued to a customer or received from a supplier.
 * @param status            the current lifecycle status of the invoice.
 * @param installmentCount  the number of installments composing the invoice.
 */
public record InvoiceDto(UUID id, UUID companyId, UUID partyId, PartyType direction, InvoiceStatus status,
        int installmentCount) {
}
