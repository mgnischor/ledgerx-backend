package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.RecurringTransactionRuleJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link RecurringTransactionRuleJpaEntity}.
 */
public interface RecurringTransactionRuleJpaRepository
        extends JpaRepository<RecurringTransactionRuleJpaEntity, UUID> {

    /**
     * Finds all recurring transaction rule entities belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of matching rule entities
     */
    List<RecurringTransactionRuleJpaEntity> findAllByCompanyId(UUID companyId);

    /**
     * Finds all active recurring transaction rule entities belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of matching active rule entities
     */
    List<RecurringTransactionRuleJpaEntity> findAllByCompanyIdAndActiveTrue(UUID companyId);
}
