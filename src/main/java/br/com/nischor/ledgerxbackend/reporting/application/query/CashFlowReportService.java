package br.com.nischor.ledgerxbackend.reporting.application.query;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CashFlowReportService {

    private final FinancialAccountRepository financialAccountRepository;
    private final TransactionRepository transactionRepository;

    public CashFlowReportService(FinancialAccountRepository financialAccountRepository,
            TransactionRepository transactionRepository) {
        this.financialAccountRepository = financialAccountRepository;
        this.transactionRepository = transactionRepository;
    }

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
