package br.com.nischor.ledgerxbackend.billing.application.usecase;

import br.com.nischor.ledgerxbackend.billing.application.dto.PartyDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.PartyMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Application use case that creates a new party (customer or supplier).
 */
@Service
public class CreatePartyUseCase {

    private final PartyRepository partyRepository;
    private final PartyMapper partyMapper;

    /**
     * Creates the use case.
     *
     * @param partyRepository repository used to persist parties.
     * @param partyMapper     mapper used to convert the party to its DTO representation.
     */
    public CreatePartyUseCase(PartyRepository partyRepository, PartyMapper partyMapper) {
        this.partyRepository = partyRepository;
        this.partyMapper = partyMapper;
    }

    /**
     * Creates and persists a new party.
     *
     * @param companyId the identifier of the company the party belongs to.
     * @param name      the party's display name.
     * @param document  the party's document number.
     * @param rawEmail  the party's email address, in raw string form; validated when building the {@link EmailAddress}.
     * @param type      whether the party is a customer or a supplier.
     * @return the DTO of the newly created party.
     */
    public PartyDto execute(UUID companyId, String name, DocumentNumber document, String rawEmail, PartyType type) {
        var party = new Party(UUID.randomUUID(), companyId, name, document, new EmailAddress(rawEmail), type);
        return partyMapper.toDto(partyRepository.save(party));
    }
}
