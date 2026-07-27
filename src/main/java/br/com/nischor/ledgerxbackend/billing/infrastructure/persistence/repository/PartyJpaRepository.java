package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.PartyJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link PartyJpaEntity} persistence operations.
 */
public interface PartyJpaRepository extends JpaRepository<PartyJpaEntity, UUID> {

    /**
     * Finds all parties belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the parties belonging to the company
     */
    List<PartyJpaEntity> findAllByCompanyId(UUID companyId);
}
