package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.NotOlderThan;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull UUID financialAccountId,
        @NotNull UUID categoryId,
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        @Size(max = 255) String description,
        @NotNull @PastOrPresent @NotOlderThan(years = 5) LocalDate occurredOn) {
}
