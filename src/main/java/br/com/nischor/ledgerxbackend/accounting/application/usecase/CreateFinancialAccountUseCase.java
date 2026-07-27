package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Creates a new financial account for a company with a given opening balance.
 */
@Service
public class CreateFinancialAccountUseCase {

    private final FinancialAccountRepository financialAccountRepository;
    private final FinancialAccountMapper financialAccountMapper;

    /**
     * Creates the use case.
     *
     * @param financialAccountRepository repository used to persist the financial account
     * @param financialAccountMapper mapper used to convert the saved account into a DTO
     */
    public CreateFinancialAccountUseCase(FinancialAccountRepository financialAccountRepository,
            FinancialAccountMapper financialAccountMapper) {
        this.financialAccountRepository = financialAccountRepository;
        this.financialAccountMapper = financialAccountMapper;
    }

    /**
     * Creates a new financial account.
     *
     * @param companyId the identifier of the company the account belongs to
     * @param name the account name
     * @param openingBalance the initial account balance
     * @return the created financial account as a DTO
     */
    public FinancialAccountDto execute(UUID companyId, String name, Money openingBalance) {
        var account = new FinancialAccount(UUID.randomUUID(), companyId, name, openingBalance);
        return financialAccountMapper.toDto(financialAccountRepository.save(account));
    }
}
