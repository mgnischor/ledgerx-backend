package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.BudgetJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetJpaRepository extends JpaRepository<BudgetJpaEntity, UUID> {

    List<BudgetJpaEntity> findAllByCompanyId(UUID companyId);

    Optional<BudgetJpaEntity> findByCompanyIdAndCategoryIdAndPeriodYearAndPeriodMonth(UUID companyId,
            UUID categoryId, int periodYear, int periodMonth);
}
