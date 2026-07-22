package br.com.nischor.ledgerxbackend.billing.domain.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID id);

    List<Invoice> findAllOpenByCompanyId(UUID companyId);
}
