package br.com.nischor.ledgerxbackend.accounting.application.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Application-layer data transfer object representing the spending status of a budget.
 *
 * @param budgetId the budget identifier
 * @param categoryId the identifier of the category the budget applies to
 * @param period the year and month the budget covers
 * @param limit the maximum amount allowed to be spent in the period
 * @param spent the amount already spent in the period
 * @param remaining the amount still available before the limit is reached
 * @param overBudget whether the spent amount has exceeded the limit
 */
public record BudgetStatusDto(UUID budgetId, UUID categoryId, YearMonth period, BigDecimal limit,
        BigDecimal spent, BigDecimal remaining, boolean overBudget) {
}
