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

/**
 * REST request payload for recording a transaction.
 *
 * @param financialAccountId the identifier of the financial account the transaction is posted to
 * @param categoryId the identifier of the category the transaction belongs to
 * @param type the transaction type (income or expense)
 * @param amount the transaction amount; must be strictly positive
 * @param description a free-text description of the transaction; up to 255 characters
 * @param occurredOn the date the transaction occurred; must not be in the future or more than 5 years in the
 *         past
 */
public record CreateTransactionRequest(
        @NotNull UUID financialAccountId,
        @NotNull UUID categoryId,
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        @Size(max = 255) String description,
        @NotNull @PastOrPresent @NotOlderThan(years = 5) LocalDate occurredOn) {
}
