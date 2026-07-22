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

@Service
public class CreatePartyUseCase {

    private final PartyRepository partyRepository;
    private final PartyMapper partyMapper;

    public CreatePartyUseCase(PartyRepository partyRepository, PartyMapper partyMapper) {
        this.partyRepository = partyRepository;
        this.partyMapper = partyMapper;
    }

    public PartyDto execute(UUID companyId, String name, DocumentNumber document, String rawEmail, PartyType type) {
        var party = new Party(UUID.randomUUID(), companyId, name, document, new EmailAddress(rawEmail), type);
        return partyMapper.toDto(partyRepository.save(party));
    }
}
