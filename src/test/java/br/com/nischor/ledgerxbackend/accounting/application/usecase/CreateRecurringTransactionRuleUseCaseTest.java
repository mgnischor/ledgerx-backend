package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRecurringTransactionRuleUseCaseTest {

    @Mock
    private RecurringTransactionRuleRepository recurringTransactionRuleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RecurringTransactionRuleMapper mapper;

    private CreateRecurringTransactionRuleUseCase useCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final Money amount = Money.brl(new BigDecimal("100.00"));

    @BeforeEach
    void setUp() {
        useCase = new CreateRecurringTransactionRuleUseCase(recurringTransactionRuleRepository, categoryRepository,
                mapper);
    }

    @Test
    void createsRuleWhenCategoryTypeMatches() {
        var category = new Category(categoryId, companyId, "Rent", TransactionType.EXPENSE);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(recurringTransactionRuleRepository.save(any(RecurringTransactionRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new RecurringTransactionRuleDto(UUID.randomUUID(), companyId, accountId, categoryId,
                TransactionType.EXPENSE, amount.amount(), "Rent", RecurrenceFrequency.MONTHLY, LocalDate.now(), true);
        when(mapper.toDto(any(RecurringTransactionRule.class))).thenReturn(dto);

        var result = useCase.execute(companyId, accountId, categoryId, TransactionType.EXPENSE, amount, "Rent",
                RecurrenceFrequency.MONTHLY, LocalDate.now());

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void rejectsUnknownCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(companyId, accountId, categoryId, TransactionType.EXPENSE, amount,
                "Rent", RecurrenceFrequency.MONTHLY, LocalDate.now())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsCategoryTypeMismatch() {
        var category = new Category(categoryId, companyId, "Salary", TransactionType.INCOME);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(companyId, accountId, categoryId, TransactionType.EXPENSE, amount,
                "Rent", RecurrenceFrequency.MONTHLY, LocalDate.now()))
                .isInstanceOf(BusinessRuleViolationException.class);
        verify(recurringTransactionRuleRepository, never()).save(any());
    }
}
