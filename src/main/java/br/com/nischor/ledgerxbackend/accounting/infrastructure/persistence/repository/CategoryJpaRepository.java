package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.CategoryJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link CategoryJpaEntity}.
 */
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    /**
     * Finds all category entities belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of matching category entities
     */
    List<CategoryJpaEntity> findAllByCompanyId(UUID companyId);
}
