package br.com.nischor.ledgerxbackend.billing.domain.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link Party} entities.
 */
public interface PartyRepository {

    /**
     * Persists the given party, creating or updating it as needed.
     *
     * @param party the party to save.
     * @return the persisted party.
     */
    Party save(Party party);

    /**
     * Finds a party by its identifier.
     *
     * @param id the party identifier.
     * @return an {@link Optional} containing the party if found, or empty otherwise.
     */
    Optional<Party> findById(UUID id);

    /**
     * Finds all parties belonging to a company.
     *
     * @param companyId the identifier of the company.
     * @return the list of parties for the company.
     */
    List<Party> findAllByCompanyId(UUID companyId);
}
