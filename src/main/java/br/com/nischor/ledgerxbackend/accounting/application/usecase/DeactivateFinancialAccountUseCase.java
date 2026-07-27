package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Deactivates an existing financial account.
 */
@Service
public class DeactivateFinancialAccountUseCase {

    private final FinancialAccountRepository financialAccountRepository;
    private final FinancialAccountMapper financialAccountMapper;

    /**
     * Creates the use case.
     *
     * @param financialAccountRepository repository used to look up and persist the account
     * @param financialAccountMapper mapper used to convert the saved account into a DTO
     */
    public DeactivateFinancialAccountUseCase(FinancialAccountRepository financialAccountRepository,
            FinancialAccountMapper financialAccountMapper) {
        this.financialAccountRepository = financialAccountRepository;
        this.financialAccountMapper = financialAccountMapper;
    }

    /**
     * Deactivates the financial account with the given identifier.
     *
     * @param accountId the identifier of the financial account to deactivate
     * @return the deactivated financial account as a DTO
     * @throws EntityNotFoundException if no financial account exists with the given identifier
     */
    public FinancialAccountDto execute(UUID accountId) {
        var account = financialAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, accountId));
        account.deactivate();
        return financialAccountMapper.toDto(financialAccountRepository.save(account));
    }
}
