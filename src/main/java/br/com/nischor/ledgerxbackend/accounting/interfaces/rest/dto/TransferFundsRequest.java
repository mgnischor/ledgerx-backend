package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.FieldsNotEqual;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@FieldsNotEqual(first = "fromAccountId", second = "toAccountId",
        message = "the source and destination accounts must be different")
public record TransferFundsRequest(
        @NotNull UUID fromAccountId,
        @NotNull UUID toAccountId,
        @NotNull @Positive BigDecimal amount) {
}
