package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.FinancialAccountJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialAccountJpaRepository extends JpaRepository<FinancialAccountJpaEntity, UUID> {

    List<FinancialAccountJpaEntity> findAllByCompanyId(UUID companyId);
}
