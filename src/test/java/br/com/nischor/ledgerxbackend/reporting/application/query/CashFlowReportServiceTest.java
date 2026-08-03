package br.com.nischor.ledgerxbackend.reporting.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CashFlowReportServiceTest {

    @Mock
    private FinancialAccountRepository financialAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private CashFlowReportService service;

    private final UUID companyId = UUID.randomUUID();
    private final LocalDate from = LocalDate.now().minusDays(30);
    private final LocalDate to = LocalDate.now();

    @BeforeEach
    void setUp() {
        service = new CashFlowReportService(financialAccountRepository, transactionRepository);
    }

    @Test
    void aggregatesIncomeAndExpenseAcrossAllCompanyAccounts() {
        var account1 = new FinancialAccount(UUID.randomUUID(), companyId, "Checking",
                Money.brl(new BigDecimal("100.00")));
        var account2 = new FinancialAccount(UUID.randomUUID(), companyId, "Savings",
                Money.brl(new BigDecimal("200.00")));
        when(financialAccountRepository.findAllByCompanyId(companyId)).thenReturn(List.of(account1, account2));

        var income = new Transaction(UUID.randomUUID(), account1.getId(), UUID.randomUUID(), TransactionType.INCOME,
                Money.brl(new BigDecimal("500.00")), "Salary", from);
        var expense = new Transaction(UUID.randomUUID(), account1.getId(), UUID.randomUUID(),
                TransactionType.EXPENSE, Money.brl(new BigDecimal("150.00")), "Groceries", from);
        when(transactionRepository.findByFinancialAccountIdAndPeriod(account1.getId(), from, to))
                .thenReturn(List.of(income, expense));
        when(transactionRepository.findByFinancialAccountIdAndPeriod(account2.getId(), from, to))
                .thenReturn(List.of());

        var result = service.summarize(companyId, from, to);

        assertThat(result.totalIncome()).isEqualByComparingTo("500.00");
        assertThat(result.totalExpense()).isEqualByComparingTo("150.00");
        assertThat(result.netResult()).isEqualByComparingTo("350.00");
    }

    @Test
    void excludesTransferTransactionsFromTotals() {
        var account = new FinancialAccount(UUID.randomUUID(), companyId, "Checking",
                Money.brl(new BigDecimal("100.00")));
        when(financialAccountRepository.findAllByCompanyId(companyId)).thenReturn(List.of(account));

        var transfer = new Transaction(UUID.randomUUID(), account.getId(), UUID.randomUUID(),
                TransactionType.TRANSFER, Money.brl(new BigDecimal("999.00")), "Transfer", from);
        when(transactionRepository.findByFinancialAccountIdAndPeriod(account.getId(), from, to))
                .thenReturn(List.of(transfer));

        var result = service.summarize(companyId, from, to);

        assertThat(result.totalIncome()).isEqualByComparingTo("0");
        assertThat(result.totalExpense()).isEqualByComparingTo("0");
        assertThat(result.netResult()).isEqualByComparingTo("0");
    }

    @Test
    void returnsZeroedSummaryWhenCompanyHasNoAccounts() {
        when(financialAccountRepository.findAllByCompanyId(companyId)).thenReturn(List.of());

        var result = service.summarize(companyId, from, to);

        assertThat(result.totalIncome()).isEqualByComparingTo("0");
        assertThat(result.totalExpense()).isEqualByComparingTo("0");
        assertThat(result.netResult()).isEqualByComparingTo("0");
    }
}
