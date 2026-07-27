package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.BudgetJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link BudgetJpaEntity}.
 */
public interface BudgetJpaRepository extends JpaRepository<BudgetJpaEntity, UUID> {

    /**
     * Finds all budget entities belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of matching budget entities
     */
    List<BudgetJpaEntity> findAllByCompanyId(UUID companyId);

    /**
     * Finds the budget entity for a specific company, category, and period.
     *
     * @param companyId the identifier of the company
     * @param categoryId the identifier of the category
     * @param periodYear the year of the budget period
     * @param periodMonth the month of the budget period
     * @return an {@link Optional} containing the matching entity if found, or empty otherwise
     */
    Optional<BudgetJpaEntity> findByCompanyIdAndCategoryIdAndPeriodYearAndPeriodMonth(UUID companyId,
            UUID categoryId, int periodYear, int periodMonth);
}
