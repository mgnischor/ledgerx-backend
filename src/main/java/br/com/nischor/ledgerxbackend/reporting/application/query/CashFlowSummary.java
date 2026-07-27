package br.com.nischor.ledgerxbackend.reporting.application.query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Read-only projection representing the cash-flow summary of a company over a given period.
 *
 * @param companyId    identifier of the company the summary belongs to
 * @param from         start date of the reporting period (inclusive)
 * @param to           end date of the reporting period (inclusive)
 * @param totalIncome  sum of all INCOME transaction amounts within the period
 * @param totalExpense sum of all EXPENSE transaction amounts within the period
 * @param netResult    difference between total income and total expense
 */
public record CashFlowSummary(UUID companyId, LocalDate from, LocalDate to, BigDecimal totalIncome,
        BigDecimal totalExpense, BigDecimal netResult) {
}
