package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
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
class DeactivateRecurringTransactionRuleUseCaseTest {

    @Mock
    private RecurringTransactionRuleRepository recurringTransactionRuleRepository;

    @Mock
    private RecurringTransactionRuleMapper mapper;

    private DeactivateRecurringTransactionRuleUseCase useCase;

    private final UUID ruleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DeactivateRecurringTransactionRuleUseCase(recurringTransactionRuleRepository, mapper);
    }

    @Test
    void deactivatesExistingRule() {
        var rule = new RecurringTransactionRule(ruleId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.EXPENSE, Money.brl(new BigDecimal("100.00")), "Rent", RecurrenceFrequency.MONTHLY,
                LocalDate.now());
        when(recurringTransactionRuleRepository.findById(ruleId)).thenReturn(Optional.of(rule));
        when(recurringTransactionRuleRepository.save(rule)).thenReturn(rule);
        var dto = new RecurringTransactionRuleDto(ruleId, rule.getCompanyId(), rule.getFinancialAccountId(),
                rule.getCategoryId(), TransactionType.EXPENSE, rule.getAmount().amount(), "Rent",
                RecurrenceFrequency.MONTHLY, rule.getNextOccurrence(), false);
        when(mapper.toDto(rule)).thenReturn(dto);

        var result = useCase.execute(ruleId);

        assertThat(result.active()).isFalse();
        assertThat(rule.isActive()).isFalse();
    }

    @Test
    void rejectsUnknownRule() {
        when(recurringTransactionRuleRepository.findById(ruleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ruleId)).isInstanceOf(EntityNotFoundException.class);
    }
}
