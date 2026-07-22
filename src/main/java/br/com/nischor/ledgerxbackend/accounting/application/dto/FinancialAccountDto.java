package br.com.nischor.ledgerxbackend.accounting.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FinancialAccountDto(UUID id, UUID companyId, String name, BigDecimal balance, String currency,
        boolean active) {
}
