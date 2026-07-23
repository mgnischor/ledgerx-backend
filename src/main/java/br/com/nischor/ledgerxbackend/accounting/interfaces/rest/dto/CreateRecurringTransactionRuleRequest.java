package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateRecurringTransactionRuleRequest(
        @NotNull UUID financialAccountId,
        @NotNull UUID categoryId,
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        @Size(max = 255) String description,
        @NotNull RecurrenceFrequency frequency,
        @NotNull @FutureOrPresent(message = "firstOccurrence cannot be in the past") LocalDate firstOccurrence) {
}
