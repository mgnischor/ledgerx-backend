package br.com.nischor.ledgerxbackend.company.domain.repository;

import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for persisting and retrieving {@link Company} domain entities, decoupling the
 * domain layer from the underlying persistence technology.
 */
public interface CompanyRepository {

    /**
     * Persists the given company, creating or updating it as needed.
     *
     * @param company the company to save
     * @return the saved company
     */
    Company save(Company company);

    /**
     * Finds a company by its unique identifier.
     *
     * @param id identifier of the company
     * @return an {@link Optional} containing the company if found, empty otherwise
     */
    Optional<Company> findById(UUID id);

    /**
     * Finds a company by its CNPJ document number.
     *
     * @param cnpj the CNPJ to search for
     * @return an {@link Optional} containing the company if found, empty otherwise
     */
    Optional<Company> findByCnpj(DocumentNumber cnpj);

    /**
     * Checks whether a company with the given CNPJ already exists.
     *
     * @param cnpj the CNPJ to check
     * @return {@code true} if a company with this CNPJ exists, {@code false} otherwise
     */
    boolean existsByCnpj(DocumentNumber cnpj);

    /**
     * Counts the total number of registered companies.
     *
     * @return the number of companies
     */
    long count();

    /**
     * Retrieves every registered company.
     *
     * @return all companies, in no particular order
     */
    List<Company> findAll();
}
