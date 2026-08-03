package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBudgetStatusUseCaseTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private GetBudgetStatusUseCase useCase;

    private final UUID budgetId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final YearMonth period = YearMonth.now();

    @BeforeEach
    void setUp() {
        useCase = new GetBudgetStatusUseCase(budgetRepository, transactionRepository);
    }

    @Test
    void computesRemainingBudgetWhenUnderLimit() {
        var budget = new Budget(budgetId, UUID.randomUUID(), categoryId, period, Money.brl(new BigDecimal("500.00")));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        var transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("100.00")), "Groceries", period.atDay(1));
        when(transactionRepository.findByCategoryIdAndPeriod(categoryId, period.atDay(1), period.atEndOfMonth()))
                .thenReturn(List.of(transaction));

        var result = useCase.execute(budgetId);

        assertThat(result.spent()).isEqualByComparingTo("100.00");
        assertThat(result.remaining()).isEqualByComparingTo("400.00");
        assertThat(result.overBudget()).isFalse();
    }

    @Test
    void flagsOverBudgetWhenSpentExceedsLimit() {
        var budget = new Budget(budgetId, UUID.randomUUID(), categoryId, period, Money.brl(new BigDecimal("100.00")));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        var transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("150.00")), "Groceries", period.atDay(1));
        when(transactionRepository.findByCategoryIdAndPeriod(categoryId, period.atDay(1), period.atEndOfMonth()))
                .thenReturn(List.of(transaction));

        var result = useCase.execute(budgetId);

        assertThat(result.overBudget()).isTrue();
    }

    @Test
    void rejectsUnknownBudget() {
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(budgetId)).isInstanceOf(EntityNotFoundException.class);
    }
}
