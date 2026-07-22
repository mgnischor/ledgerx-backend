package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeactivateFinancialAccountUseCase {

    private final FinancialAccountRepository financialAccountRepository;
    private final FinancialAccountMapper financialAccountMapper;

    public DeactivateFinancialAccountUseCase(FinancialAccountRepository financialAccountRepository,
            FinancialAccountMapper financialAccountMapper) {
        this.financialAccountRepository = financialAccountRepository;
        this.financialAccountMapper = financialAccountMapper;
    }

    public FinancialAccountDto execute(UUID accountId) {
        var account = financialAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, accountId));
        account.deactivate();
        return financialAccountMapper.toDto(financialAccountRepository.save(account));
    }
}
