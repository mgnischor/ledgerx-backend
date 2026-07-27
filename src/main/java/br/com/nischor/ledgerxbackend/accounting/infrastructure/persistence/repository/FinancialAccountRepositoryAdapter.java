package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper.FinancialAccountJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter implementing {@link FinancialAccountRepository} on top of Spring Data JPA, translating between
 * domain {@link FinancialAccount} objects and {@link FinancialAccountJpaEntity} instances via
 * {@link FinancialAccountJpaMapper}.
 */
@Repository
public class FinancialAccountRepositoryAdapter implements FinancialAccountRepository {

    private final FinancialAccountJpaRepository jpaRepository;
    private final FinancialAccountJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository the underlying Spring Data JPA repository
     * @param mapper mapper used to convert between domain and persistence representations
     */
    public FinancialAccountRepositoryAdapter(FinancialAccountJpaRepository jpaRepository,
            FinancialAccountJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public FinancialAccount save(FinancialAccount account) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(account)));
    }

    @Override
    public Optional<FinancialAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<FinancialAccount> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }
}
