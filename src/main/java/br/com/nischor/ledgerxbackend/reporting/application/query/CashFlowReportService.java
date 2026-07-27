package br.com.nischor.ledgerxbackend.reporting.application.query;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Application query service that computes cash-flow summaries for a company over a period.
 *
 * <p>Aggregates income and expense transactions across all of a company's financial accounts,
 * excluding transfers, to produce a {@link CashFlowSummary}.
 */
@Service
public class CashFlowReportService {

    private final FinancialAccountRepository financialAccountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Creates a new {@code CashFlowReportService}.
     *
     * @param financialAccountRepository repository used to look up a company's financial accounts
     * @param transactionRepository      repository used to look up transactions within a period
     */
    public CashFlowReportService(FinancialAccountRepository financialAccountRepository,
            TransactionRepository transactionRepository) {
        this.financialAccountRepository = financialAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Computes the cash-flow summary for the given company and period.
     *
     * <p>Only {@code INCOME} and {@code EXPENSE} transactions are considered; other transaction
     * types (e.g. transfers) are excluded to avoid double counting.
     *
     * @param companyId identifier of the company whose accounts are summarized
     * @param from      start date of the reporting period (inclusive)
     * @param to        end date of the reporting period (inclusive)
     * @return the computed {@link CashFlowSummary} with total income, total expense and net result
     */
    public CashFlowSummary summarize(UUID companyId, LocalDate from, LocalDate to) {
        var transactions = financialAccountRepository.findAllByCompanyId(companyId).stream()
                .flatMap(account -> transactionRepository
                        .findByFinancialAccountIdAndPeriod(account.getId(), from, to)
                        .stream())
                .toList();

        var totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(t -> t.getAmount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(t -> t.getAmount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CashFlowSummary(companyId, from, to, totalIncome, totalExpense,
                totalIncome.subtract(totalExpense));
    }
}
