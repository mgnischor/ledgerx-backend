package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper.BudgetJpaMapper;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter implementing {@link BudgetRepository} on top of Spring Data JPA, translating between domain
 * {@link Budget} objects and {@link BudgetJpaEntity} instances via {@link BudgetJpaMapper}.
 */
@Repository
public class BudgetRepositoryAdapter implements BudgetRepository {

    private final BudgetJpaRepository jpaRepository;
    private final BudgetJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository the underlying Spring Data JPA repository
     * @param mapper mapper used to convert between domain and persistence representations
     */
    public BudgetRepositoryAdapter(BudgetJpaRepository jpaRepository, BudgetJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Budget save(Budget budget) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(budget)));
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Budget> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Budget> findByCompanyIdAndCategoryIdAndPeriod(UUID companyId, UUID categoryId,
            YearMonth period) {
        return jpaRepository
                .findByCompanyIdAndCategoryIdAndPeriodYearAndPeriodMonth(companyId, categoryId, period.getYear(),
                        period.getMonthValue())
                .map(mapper::toDomain);
    }
}
