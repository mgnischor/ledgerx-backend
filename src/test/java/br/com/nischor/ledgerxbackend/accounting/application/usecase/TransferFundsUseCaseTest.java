package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferFundsUseCaseTest {

    @Mock
    private FinancialAccountRepository financialAccountRepository;

    private TransferFundsUseCase useCase;

    private final UUID fromAccountId = UUID.randomUUID();
    private final UUID toAccountId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new TransferFundsUseCase(financialAccountRepository);
    }

    @Test
    void movesFundsBetweenBothAccounts() {
        var source = new FinancialAccount(fromAccountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("500.00")));
        var destination = new FinancialAccount(toAccountId, UUID.randomUUID(), "Savings",
                Money.brl(new BigDecimal("100.00")));
        when(financialAccountRepository.findById(fromAccountId)).thenReturn(Optional.of(source));
        when(financialAccountRepository.findById(toAccountId)).thenReturn(Optional.of(destination));

        useCase.execute(fromAccountId, toAccountId, Money.brl(new BigDecimal("200.00")));

        assertThat(source.getBalance().amount()).isEqualByComparingTo("300.00");
        assertThat(destination.getBalance().amount()).isEqualByComparingTo("300.00");
        verify(financialAccountRepository).save(source);
        verify(financialAccountRepository).save(destination);
    }

    @Test
    void rejectsUnknownSourceAccount() {
        when(financialAccountRepository.findById(fromAccountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(fromAccountId, toAccountId, Money.brl(new BigDecimal("50.00"))))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsUnknownDestinationAccount() {
        var source = new FinancialAccount(fromAccountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("500.00")));
        when(financialAccountRepository.findById(fromAccountId)).thenReturn(Optional.of(source));
        when(financialAccountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(fromAccountId, toAccountId, Money.brl(new BigDecimal("50.00"))))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void rejectsTransferExceedingSourceBalance() {
        var source = new FinancialAccount(fromAccountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("10.00")));
        var destination = new FinancialAccount(toAccountId, UUID.randomUUID(), "Savings",
                Money.brl(new BigDecimal("100.00")));
        when(financialAccountRepository.findById(fromAccountId)).thenReturn(Optional.of(source));
        when(financialAccountRepository.findById(toAccountId)).thenReturn(Optional.of(destination));

        assertThatThrownBy(() -> useCase.execute(fromAccountId, toAccountId, Money.brl(new BigDecimal("50.00"))))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
