package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper.PartyJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter implementing the domain {@link PartyRepository} port on top of Spring Data JPA,
 * translating between domain parties and JPA entities via {@link PartyJpaMapper}.
 */
@Repository
public class PartyRepositoryAdapter implements PartyRepository {

    private final PartyJpaRepository jpaRepository;
    private final PartyJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository the underlying Spring Data JPA repository
     * @param mapper the mapper used to convert between domain and JPA representations
     */
    public PartyRepositoryAdapter(PartyJpaRepository jpaRepository, PartyJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Persists the given party and returns the saved domain representation.
     *
     * @param party the party to save
     * @return the persisted party, converted back to its domain representation
     */
    @Override
    public Party save(Party party) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(party)));
    }

    /**
     * Finds a party by its identifier.
     *
     * @param id the party identifier
     * @return the matching party, or an empty {@link Optional} if none is found
     */
    @Override
    public Optional<Party> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * Finds all parties belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the parties belonging to the company
     */
    @Override
    public List<Party> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }
}
