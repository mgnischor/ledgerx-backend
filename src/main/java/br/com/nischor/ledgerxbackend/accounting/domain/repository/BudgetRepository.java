package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link Budget} aggregates.
 */
public interface BudgetRepository {

    /**
     * Persists a budget.
     *
     * @param budget the budget to save
     * @return the saved budget
     */
    Budget save(Budget budget);

    /**
     * Finds a budget by its identifier.
     *
     * @param id the budget identifier
     * @return an {@link Optional} containing the budget if found, or empty otherwise
     */
    Optional<Budget> findById(UUID id);

    /**
     * Finds all budgets belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of budgets owned by the company
     */
    List<Budget> findAllByCompanyId(UUID companyId);

    /**
     * Finds the budget for a specific company, category, and period.
     *
     * @param companyId the identifier of the company
     * @param categoryId the identifier of the category
     * @param period the year and month the budget covers
     * @return an {@link Optional} containing the matching budget if found, or empty otherwise
     */
    Optional<Budget> findByCompanyIdAndCategoryIdAndPeriod(UUID companyId, UUID categoryId, YearMonth period);
}
