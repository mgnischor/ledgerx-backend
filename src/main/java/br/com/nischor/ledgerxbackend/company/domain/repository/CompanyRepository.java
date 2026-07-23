package br.com.nischor.ledgerxbackend.company.domain.repository;

import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(UUID id);

    Optional<Company> findByCnpj(DocumentNumber cnpj);

    boolean existsByCnpj(DocumentNumber cnpj);

    long count();
}
