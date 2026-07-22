package br.com.nischor.ledgerxbackend.billing.application.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import java.util.UUID;

public record PartyDto(UUID id, UUID companyId, String name, String document, String email, PartyType type) {
}
