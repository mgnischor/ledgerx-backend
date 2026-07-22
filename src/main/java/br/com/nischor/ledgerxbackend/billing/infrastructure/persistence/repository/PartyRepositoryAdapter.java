package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper.PartyJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PartyRepositoryAdapter implements PartyRepository {

    private final PartyJpaRepository jpaRepository;
    private final PartyJpaMapper mapper;

    public PartyRepositoryAdapter(PartyJpaRepository jpaRepository, PartyJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Party save(Party party) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(party)));
    }

    @Override
    public Optional<Party> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Party> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }
}
