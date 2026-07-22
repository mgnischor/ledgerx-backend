package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateFinancialAccountRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 100) String name,
        @NotNull @PositiveOrZero BigDecimal openingBalance) {
}
