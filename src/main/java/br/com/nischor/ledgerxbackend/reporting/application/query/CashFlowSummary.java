package br.com.nischor.ledgerxbackend.reporting.application.query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CashFlowSummary(UUID companyId, LocalDate from, LocalDate to, BigDecimal totalIncome,
        BigDecimal totalExpense, BigDecimal netResult) {
}
