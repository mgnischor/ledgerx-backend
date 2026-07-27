package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

/**
 * REST request payload for creating a budget.
 *
 * @param categoryId the identifier of the category the budget applies to
 * @param period the year and month the budget covers; must not be in the past
 * @param limit the maximum amount allowed to be spent in the period; must be strictly positive
 */
public record CreateBudgetRequest(
        @NotNull UUID categoryId,
        @NotNull @FutureOrPresent(message = "period must not be in the past") YearMonth period,
        @NotNull @Positive BigDecimal limit) {
}
