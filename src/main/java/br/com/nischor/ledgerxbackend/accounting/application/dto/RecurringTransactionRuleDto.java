package br.com.nischor.ledgerxbackend.accounting.application.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecurringTransactionRuleDto(UUID id, UUID companyId, UUID financialAccountId, UUID categoryId,
        TransactionType type, BigDecimal amount, String description, RecurrenceFrequency frequency,
        LocalDate nextOccurrence, boolean active) {
}
