package br.com.nischor.ledgerxbackend.billing.domain.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link Invoice} aggregates.
 */
public interface InvoiceRepository {

    /**
     * Persists the given invoice, creating or updating it as needed.
     *
     * @param invoice the invoice to save.
     * @return the persisted invoice.
     */
    Invoice save(Invoice invoice);

    /**
     * Finds an invoice by its identifier.
     *
     * @param id the invoice identifier.
     * @return an {@link Optional} containing the invoice if found, or empty otherwise.
     */
    Optional<Invoice> findById(UUID id);

    /**
     * Finds all invoices belonging to a company that are currently in {@code OPEN} status.
     *
     * @param companyId the identifier of the company.
     * @return the list of open invoices for the company.
     */
    List<Invoice> findAllOpenByCompanyId(UUID companyId);
}
