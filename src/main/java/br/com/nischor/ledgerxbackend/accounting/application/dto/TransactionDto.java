package br.com.nischor.ledgerxbackend.accounting.application.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Application-layer data transfer object representing a financial transaction.
 *
 * @param id the transaction identifier
 * @param financialAccountId the identifier of the financial account the transaction is posted to
 * @param categoryId the identifier of the category the transaction belongs to
 * @param type the transaction type (income or expense)
 * @param amount the transaction amount
 * @param description a free-text description of the transaction
 * @param occurredOn the date the transaction occurred
 */
public record TransactionDto(UUID id, UUID financialAccountId, UUID categoryId, TransactionType type,
        BigDecimal amount, String description, LocalDate occurredOn) {
}
