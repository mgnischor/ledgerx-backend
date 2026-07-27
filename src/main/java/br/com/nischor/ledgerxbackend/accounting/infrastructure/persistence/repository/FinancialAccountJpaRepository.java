package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.FinancialAccountJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link FinancialAccountJpaEntity}.
 */
public interface FinancialAccountJpaRepository extends JpaRepository<FinancialAccountJpaEntity, UUID> {

    /**
     * Finds all financial account entities belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of matching financial account entities
     */
    List<FinancialAccountJpaEntity> findAllByCompanyId(UUID companyId);
}
