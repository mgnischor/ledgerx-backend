package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper.InvoiceJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository jpaRepository;
    private final InvoiceJpaMapper mapper;

    public InvoiceRepositoryAdapter(InvoiceJpaRepository jpaRepository, InvoiceJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(invoice)));
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Invoice> findAllOpenByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyIdAndStatusNot(companyId, InvoiceStatus.CANCELED).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
