package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link FinancialAccount} aggregates.
 */
public interface FinancialAccountRepository {

    /**
     * Persists a financial account.
     *
     * @param account the financial account to save
     * @return the saved financial account
     */
    FinancialAccount save(FinancialAccount account);

    /**
     * Finds a financial account by its identifier.
     *
     * @param id the financial account identifier
     * @return an {@link Optional} containing the account if found, or empty otherwise
     */
    Optional<FinancialAccount> findById(UUID id);

    /**
     * Finds all financial accounts belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of financial accounts owned by the company
     */
    List<FinancialAccount> findAllByCompanyId(UUID companyId);
}
