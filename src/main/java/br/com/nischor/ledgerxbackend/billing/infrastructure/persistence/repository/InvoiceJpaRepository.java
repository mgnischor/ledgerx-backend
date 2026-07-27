package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.InvoiceJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link InvoiceJpaEntity} persistence operations.
 */
public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, UUID> {

    /**
     * Finds all invoices of a company whose status does not match the given one.
     *
     * @param companyId the identifier of the company owning the invoices
     * @param status the status to exclude from the results
     * @return the matching invoice entities
     */
    List<InvoiceJpaEntity> findAllByCompanyIdAndStatusNot(UUID companyId, InvoiceStatus status);
}
