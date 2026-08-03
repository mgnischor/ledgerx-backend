package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
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
class DeactivateBudgetUseCaseTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetMapper budgetMapper;

    private DeactivateBudgetUseCase useCase;

    private final UUID budgetId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DeactivateBudgetUseCase(budgetRepository, budgetMapper);
    }

    @Test
    void deactivatesExistingBudget() {
        var budget = new Budget(budgetId, UUID.randomUUID(), UUID.randomUUID(), YearMonth.now(),
                Money.brl(new BigDecimal("500.00")));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(budget)).thenReturn(budget);
        var dto = new BudgetDto(budgetId, budget.getCompanyId(), budget.getCategoryId(), YearMonth.now(),
                budget.getLimit().amount(), "BRL", false);
        when(budgetMapper.toDto(budget)).thenReturn(dto);

        var result = useCase.execute(budgetId);

        assertThat(result.active()).isFalse();
        assertThat(budget.isActive()).isFalse();
    }

    @Test
    void rejectsUnknownBudget() {
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(budgetId)).isInstanceOf(EntityNotFoundException.class);
    }
}
