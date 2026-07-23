package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public record CreateBudgetRequest(
        @NotNull UUID categoryId,
        @NotNull @FutureOrPresent(message = "period must not be in the past") YearMonth period,
        @NotNull @Positive BigDecimal limit) {
}
