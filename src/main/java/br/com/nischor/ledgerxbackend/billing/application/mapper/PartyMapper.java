package br.com.nischor.ledgerxbackend.billing.application.mapper;

import br.com.nischor.ledgerxbackend.billing.application.dto.PartyDto;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import org.springframework.stereotype.Component;

@Component
public class PartyMapper {

    public PartyDto toDto(Party party) {
        return new PartyDto(party.getId(), party.getCompanyId(), party.getName(), party.getDocument().value(),
                party.getEmail().value(), party.getType());
    }
}
