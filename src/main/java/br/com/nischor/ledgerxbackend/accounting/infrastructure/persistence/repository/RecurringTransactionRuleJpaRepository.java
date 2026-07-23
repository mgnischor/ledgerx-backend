package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.RecurringTransactionRuleJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRuleJpaRepository
        extends JpaRepository<RecurringTransactionRuleJpaEntity, UUID> {

    List<RecurringTransactionRuleJpaEntity> findAllByCompanyId(UUID companyId);

    List<RecurringTransactionRuleJpaEntity> findAllByCompanyIdAndActiveTrue(UUID companyId);
}
