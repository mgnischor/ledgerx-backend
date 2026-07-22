package br.com.nischor.ledgerxbackend.company.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.company.infrastructure.persistence.entity.CompanyJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {

    Optional<CompanyJpaEntity> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
