package br.com.nischor.ledgerxbackend.company.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.company.infrastructure.persistence.mapper.CompanyJpaMapper;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;
    private final CompanyJpaMapper mapper;

    public CompanyRepositoryAdapter(CompanyJpaRepository jpaRepository, CompanyJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Company save(Company company) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(company)));
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Company> findByCnpj(DocumentNumber cnpj) {
        return jpaRepository.findByCnpj(cnpj.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCnpj(DocumentNumber cnpj) {
        return jpaRepository.existsByCnpj(cnpj.value());
    }
}
