package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.InvoiceJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, UUID> {

    List<InvoiceJpaEntity> findAllByCompanyIdAndStatusNot(UUID companyId, InvoiceStatus status);
}
