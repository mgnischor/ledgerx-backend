package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
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
class GenerateDueRecurringTransactionsUseCaseTest {

    @Mock
    private RecurringTransactionRuleRepository recurringTransactionRuleRepository;

    @Mock
    private RecordTransactionUseCase recordTransactionUseCase;

    private GenerateDueRecurringTransactionsUseCase useCase;

    private final UUID companyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new GenerateDueRecurringTransactionsUseCase(recurringTransactionRuleRepository,
                recordTransactionUseCase);
    }

    @Test
    void generatesTransactionAndAdvancesDueRule() {
        var rule = new RecurringTransactionRule(UUID.randomUUID(), companyId, UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.EXPENSE, Money.brl(new BigDecimal("100.00")), "Rent", RecurrenceFrequency.MONTHLY,
                LocalDate.now().minusDays(1));
        when(recurringTransactionRuleRepository.findAllByCompanyIdAndActiveTrue(companyId)).thenReturn(List.of(rule));
        var dto = new TransactionDto(UUID.randomUUID(), rule.getFinancialAccountId(), rule.getCategoryId(),
                TransactionType.EXPENSE, rule.getAmount().amount(), "Rent", LocalDate.now().minusDays(1));
        when(recordTransactionUseCase.execute(any(), any(), any(), any(), any(), any())).thenReturn(dto);
        var originalNextOccurrence = rule.getNextOccurrence();

        var result = useCase.execute(companyId);

        assertThat(result).containsExactly(dto);
        assertThat(rule.getNextOccurrence()).isEqualTo(originalNextOccurrence.plusMonths(1));
        verify(recurringTransactionRuleRepository).save(rule);
    }

    @Test
    void skipsRulesThatAreNotYetDue() {
        var rule = new RecurringTransactionRule(UUID.randomUUID(), companyId, UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.EXPENSE, Money.brl(new BigDecimal("100.00")), "Rent", RecurrenceFrequency.MONTHLY,
                LocalDate.now().plusDays(5));
        when(recurringTransactionRuleRepository.findAllByCompanyIdAndActiveTrue(companyId)).thenReturn(List.of(rule));

        var result = useCase.execute(companyId);

        assertThat(result).isEmpty();
        verify(recordTransactionUseCase, never()).execute(any(), any(), any(), any(), any(), any());
        verify(recurringTransactionRuleRepository, never()).save(any());
    }

    @Test
    void returnsEmptyListWhenNoRulesExist() {
        when(recurringTransactionRuleRepository.findAllByCompanyIdAndActiveTrue(companyId)).thenReturn(List.of());

        var result = useCase.execute(companyId);

        assertThat(result).isEmpty();
        verify(recordTransactionUseCase, times(0)).execute(any(), any(), any(), any(), any(), any());
    }
}
