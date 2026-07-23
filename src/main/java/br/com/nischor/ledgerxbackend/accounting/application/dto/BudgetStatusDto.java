package br.com.nischor.ledgerxbackend.accounting.application.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public record BudgetStatusDto(UUID budgetId, UUID categoryId, YearMonth period, BigDecimal limit,
        BigDecimal spent, BigDecimal remaining, boolean overBudget) {
}
