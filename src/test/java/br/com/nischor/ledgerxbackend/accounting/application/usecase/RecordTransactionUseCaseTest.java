package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.TransactionMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.event.TransactionRecordedEvent;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.service.AccountBalanceService;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
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
class RecordTransactionUseCaseTest {

    @Mock
    private FinancialAccountRepository financialAccountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    private RecordTransactionUseCase useCase;

    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new RecordTransactionUseCase(financialAccountRepository, categoryRepository, transactionRepository,
                new AccountBalanceService(), transactionMapper, eventPublisher);
    }

    @Test
    void recordsExpenseAndDebitsAccount() {
        var account = new FinancialAccount(accountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("500.00")));
        var category = new Category(categoryId, account.getCompanyId(), "Groceries", TransactionType.EXPENSE);
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new TransactionDto(UUID.randomUUID(), accountId, categoryId, TransactionType.EXPENSE,
                new BigDecimal("100.00"), "Groceries", LocalDate.now());
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(dto);

        var result = useCase.execute(accountId, categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("100.00")), "Groceries", LocalDate.now());

        assertThat(result).isEqualTo(dto);
        assertThat(account.getBalance().amount()).isEqualByComparingTo("400.00");
        verify(financialAccountRepository).save(account);
        verify(eventPublisher).publish(any(TransactionRecordedEvent.class));
    }

    @Test
    void rejectsUnknownAccount() {
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(accountId, categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("100.00")), "Groceries", LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsUnknownCategory() {
        var account = new FinancialAccount(accountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("500.00")));
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(accountId, categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("100.00")), "Groceries", LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsCategoryTypeMismatch() {
        var account = new FinancialAccount(accountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("500.00")));
        var category = new Category(categoryId, account.getCompanyId(), "Salary", TransactionType.INCOME);
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(accountId, categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("100.00")), "Groceries", LocalDate.now()))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void rejectsExpenseThatWouldOverdraftAccount() {
        var account = new FinancialAccount(accountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("50.00")));
        var category = new Category(categoryId, account.getCompanyId(), "Groceries", TransactionType.EXPENSE);
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(accountId, categoryId, TransactionType.EXPENSE,
                Money.brl(new BigDecimal("100.00")), "Groceries", LocalDate.now()))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
