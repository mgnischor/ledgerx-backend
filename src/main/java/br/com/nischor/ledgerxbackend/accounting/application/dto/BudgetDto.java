package br.com.nischor.ledgerxbackend.accounting.application.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Application-layer data transfer object representing a budget.
 *
 * @param id the budget identifier
 * @param companyId the identifier of the company that owns the budget
 * @param categoryId the identifier of the category the budget applies to
 * @param period the year and month the budget covers
 * @param limit the maximum amount allowed to be spent in the period
 * @param currency the ISO currency code of the limit amount
 * @param active whether the budget is currently active
 */
public record BudgetDto(UUID id, UUID companyId, UUID categoryId, YearMonth period, BigDecimal limit,
        String currency, boolean active) {
}
