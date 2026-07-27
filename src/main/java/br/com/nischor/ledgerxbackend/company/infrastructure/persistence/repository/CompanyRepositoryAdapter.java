package br.com.nischor.ledgerxbackend.company.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.company.infrastructure.persistence.mapper.CompanyJpaMapper;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter implementing the {@link CompanyRepository} domain port on top of Spring Data JPA,
 * delegating persistence to {@link CompanyJpaRepository} and conversions to
 * {@link CompanyJpaMapper}.
 */
@Repository
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;
    private final CompanyJpaMapper mapper;

    /**
     * Creates the adapter with its required collaborators.
     *
     * @param jpaRepository Spring Data JPA repository for company entities
     * @param mapper mapper between domain companies and JPA entities
     */
    public CompanyRepositoryAdapter(CompanyJpaRepository jpaRepository, CompanyJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Company save(Company company) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(company)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> findByCnpj(DocumentNumber cnpj) {
        return jpaRepository.findByCnpj(cnpj.value()).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByCnpj(DocumentNumber cnpj) {
        return jpaRepository.existsByCnpj(cnpj.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }
}
