package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper.RecurringTransactionRuleJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter implementing {@link RecurringTransactionRuleRepository} on top of Spring Data JPA, translating
 * between domain {@link RecurringTransactionRule} objects and {@link RecurringTransactionRuleJpaEntity}
 * instances via {@link RecurringTransactionRuleJpaMapper}.
 */
@Repository
public class RecurringTransactionRuleRepositoryAdapter implements RecurringTransactionRuleRepository {

    private final RecurringTransactionRuleJpaRepository jpaRepository;
    private final RecurringTransactionRuleJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository the underlying Spring Data JPA repository
     * @param mapper mapper used to convert between domain and persistence representations
     */
    public RecurringTransactionRuleRepositoryAdapter(RecurringTransactionRuleJpaRepository jpaRepository,
            RecurringTransactionRuleJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RecurringTransactionRule save(RecurringTransactionRule rule) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(rule)));
    }

    @Override
    public Optional<RecurringTransactionRule> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RecurringTransactionRule> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RecurringTransactionRule> findAllByCompanyIdAndActiveTrue(UUID companyId) {
        return jpaRepository.findAllByCompanyIdAndActiveTrue(companyId).stream().map(mapper::toDomain).toList();
    }
}
