package br.com.nischor.ledgerxbackend.company.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.company.infrastructure.persistence.entity.CompanyJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link CompanyJpaEntity}, providing CRUD operations plus
 * lookups by CNPJ.
 */
public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {

    /**
     * Finds a company entity by its CNPJ.
     *
     * @param cnpj the CNPJ document number as plain text
     * @return an {@link Optional} containing the entity if found, empty otherwise
     */
    Optional<CompanyJpaEntity> findByCnpj(String cnpj);

    /**
     * Checks whether a company entity with the given CNPJ exists.
     *
     * @param cnpj the CNPJ document number as plain text
     * @return {@code true} if a matching entity exists, {@code false} otherwise
     */
    boolean existsByCnpj(String cnpj);
}
