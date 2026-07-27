package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper.InvoiceJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter implementing the domain {@link InvoiceRepository} port on top of Spring Data JPA,
 * translating between domain invoices and JPA entities via {@link InvoiceJpaMapper}.
 */
@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository jpaRepository;
    private final InvoiceJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository the underlying Spring Data JPA repository
     * @param mapper the mapper used to convert between domain and JPA representations
     */
    public InvoiceRepositoryAdapter(InvoiceJpaRepository jpaRepository, InvoiceJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Persists the given invoice and returns the saved domain representation.
     *
     * @param invoice the invoice to save
     * @return the persisted invoice, converted back to its domain representation
     */
    @Override
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(invoice)));
    }

    /**
     * Finds an invoice by its identifier.
     *
     * @param id the invoice identifier
     * @return the matching invoice, or an empty {@link Optional} if none is found
     */
    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * Finds all invoices of a company that are not canceled.
     *
     * @param companyId the identifier of the company owning the invoices
     * @return the non-canceled invoices of the company
     */
    @Override
    public List<Invoice> findAllOpenByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyIdAndStatusNot(companyId, InvoiceStatus.CANCELED).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
