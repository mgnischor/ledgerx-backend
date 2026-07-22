package br.com.nischor.ledgerxbackend.accounting.application.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import java.util.UUID;

public record CategoryDto(UUID id, UUID companyId, String name, TransactionType type) {
}
