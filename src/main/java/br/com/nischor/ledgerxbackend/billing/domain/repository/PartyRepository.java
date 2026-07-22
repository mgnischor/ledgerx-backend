package br.com.nischor.ledgerxbackend.billing.domain.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartyRepository {

    Party save(Party party);

    Optional<Party> findById(UUID id);

    List<Party> findAllByCompanyId(UUID companyId);
}
