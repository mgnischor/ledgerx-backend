package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST request payload for creating a financial account.
 *
 * @param companyId the identifier of the company the account belongs to
 * @param name the account name; required, up to 100 characters
 * @param openingBalance the initial account balance; must not be negative
 */
public record CreateFinancialAccountRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 100) String name,
        @NotNull @PositiveOrZero BigDecimal openingBalance) {
}
