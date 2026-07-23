package br.com.nischor.ledgerxbackend.accounting.application.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public record BudgetDto(UUID id, UUID companyId, UUID categoryId, YearMonth period, BigDecimal limit,
        String currency, boolean active) {
}
