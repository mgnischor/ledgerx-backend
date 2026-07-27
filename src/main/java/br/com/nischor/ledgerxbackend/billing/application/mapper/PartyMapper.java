package br.com.nischor.ledgerxbackend.billing.application.mapper;

import br.com.nischor.ledgerxbackend.billing.application.dto.PartyDto;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Party} domain objects into {@link PartyDto} instances for use in the application layer.
 */
@Component
public class PartyMapper {

    /**
     * Maps a {@link Party} to its {@link PartyDto} representation.
     *
     * @param party the party to convert.
     * @return the resulting DTO.
     */
    public PartyDto toDto(Party party) {
        return new PartyDto(party.getId(), party.getCompanyId(), party.getName(), party.getDocument().value(),
                party.getEmail().value(), party.getType());
    }
}
