package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.PartyJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyJpaRepository extends JpaRepository<PartyJpaEntity, UUID> {

    List<PartyJpaEntity> findAllByCompanyId(UUID companyId);
}
