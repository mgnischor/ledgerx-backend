package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateFinancialAccountUseCaseTest {

    @Mock
    private FinancialAccountRepository financialAccountRepository;

    @Mock
    private FinancialAccountMapper financialAccountMapper;

    private CreateFinancialAccountUseCase useCase;

    private final UUID companyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CreateFinancialAccountUseCase(financialAccountRepository, financialAccountMapper);
    }

    @Test
    void createsAndPersistsAccountWithOpeningBalance() {
        when(financialAccountRepository.save(any(FinancialAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new FinancialAccountDto(UUID.randomUUID(), companyId, "Checking", new BigDecimal("100.00"), "BRL",
                true);
        when(financialAccountMapper.toDto(any(FinancialAccount.class))).thenReturn(dto);

        var result = useCase.execute(companyId, "Checking", Money.brl(new BigDecimal("100.00")));

        assertThat(result).isEqualTo(dto);
    }
}
