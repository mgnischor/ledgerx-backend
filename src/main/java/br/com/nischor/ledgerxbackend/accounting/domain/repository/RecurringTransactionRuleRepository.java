package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link RecurringTransactionRule} aggregates.
 */
public interface RecurringTransactionRuleRepository {

    /**
     * Persists a recurring transaction rule.
     *
     * @param rule the rule to save
     * @return the saved rule
     */
    RecurringTransactionRule save(RecurringTransactionRule rule);

    /**
     * Finds a recurring transaction rule by its identifier.
     *
     * @param id the rule identifier
     * @return an {@link Optional} containing the rule if found, or empty otherwise
     */
    Optional<RecurringTransactionRule> findById(UUID id);

    /**
     * Finds all recurring transaction rules belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of rules owned by the company
     */
    List<RecurringTransactionRule> findAllByCompanyId(UUID companyId);

    /**
     * Finds all active recurring transaction rules belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of active rules owned by the company
     */
    List<RecurringTransactionRule> findAllByCompanyIdAndActiveTrue(UUID companyId);
}
