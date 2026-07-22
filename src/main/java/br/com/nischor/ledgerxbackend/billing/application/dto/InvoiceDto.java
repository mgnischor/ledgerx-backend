package br.com.nischor.ledgerxbackend.billing.application.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import java.util.UUID;

public record InvoiceDto(UUID id, UUID companyId, UUID partyId, PartyType direction, InvoiceStatus status,
        int installmentCount) {
}
