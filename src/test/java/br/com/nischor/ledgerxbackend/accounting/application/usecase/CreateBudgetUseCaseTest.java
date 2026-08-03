package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateBudgetUseCaseTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetMapper budgetMapper;

    private CreateBudgetUseCase useCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final YearMonth period = YearMonth.now();
    private final Money limit = Money.brl(new BigDecimal("500.00"));

    @BeforeEach
    void setUp() {
        useCase = new CreateBudgetUseCase(budgetRepository, categoryRepository, budgetMapper);
    }

    @Test
    void createsBudgetForExpenseCategoryWithNoExistingBudget() {
        var category = new Category(categoryId, companyId, "Groceries", TransactionType.EXPENSE);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(budgetRepository.findByCompanyIdAndCategoryIdAndPeriod(companyId, categoryId, period))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new BudgetDto(UUID.randomUUID(), companyId, categoryId, period, limit.amount(), "BRL", true);
        when(budgetMapper.toDto(any(Budget.class))).thenReturn(dto);

        var result = useCase.execute(companyId, categoryId, period, limit);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void rejectsUnknownCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(companyId, categoryId, period, limit))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsNonExpenseCategory() {
        var category = new Category(categoryId, companyId, "Salary", TransactionType.INCOME);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(companyId, categoryId, period, limit))
                .isInstanceOf(BusinessRuleViolationException.class);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateBudgetForSamePeriod() {
        var category = new Category(categoryId, companyId, "Groceries", TransactionType.EXPENSE);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(budgetRepository.findByCompanyIdAndCategoryIdAndPeriod(companyId, categoryId, period))
                .thenReturn(Optional.of(new Budget(UUID.randomUUID(), companyId, categoryId, period, limit)));

        assertThatThrownBy(() -> useCase.execute(companyId, categoryId, period, limit))
                .isInstanceOf(BusinessRuleViolationException.class);
        verify(budgetRepository, never()).save(any());
    }
}
