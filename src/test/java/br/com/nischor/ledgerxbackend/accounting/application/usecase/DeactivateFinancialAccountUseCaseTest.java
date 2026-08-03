package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
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
class DeactivateFinancialAccountUseCaseTest {

    @Mock
    private FinancialAccountRepository financialAccountRepository;

    @Mock
    private FinancialAccountMapper financialAccountMapper;

    private DeactivateFinancialAccountUseCase useCase;

    private final UUID accountId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DeactivateFinancialAccountUseCase(financialAccountRepository, financialAccountMapper);
    }

    @Test
    void deactivatesExistingAccount() {
        var account = new FinancialAccount(accountId, UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("100.00")));
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(financialAccountRepository.save(account)).thenReturn(account);
        var dto = new FinancialAccountDto(accountId, account.getCompanyId(), "Checking", new BigDecimal("100.00"),
                "BRL", false);
        when(financialAccountMapper.toDto(account)).thenReturn(dto);

        var result = useCase.execute(accountId);

        assertThat(result.active()).isFalse();
        assertThat(account.isActive()).isFalse();
    }

    @Test
    void rejectsUnknownAccount() {
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(accountId)).isInstanceOf(EntityNotFoundException.class);
    }
}
