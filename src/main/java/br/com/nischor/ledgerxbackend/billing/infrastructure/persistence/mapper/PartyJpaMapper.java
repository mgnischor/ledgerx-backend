package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.PartyJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import org.springframework.stereotype.Component;

@Component
public class PartyJpaMapper {

    public Party toDomain(PartyJpaEntity entity) {
        var document = entity.getDocument().length() == 11
                ? DocumentNumber.cpf(entity.getDocument())
                : DocumentNumber.cnpj(entity.getDocument());
        return new Party(entity.getId(), entity.getCompanyId(), entity.getName(), document,
                new EmailAddress(entity.getEmail()), entity.getType());
    }

    public PartyJpaEntity toEntity(Party party) {
        return new PartyJpaEntity(party.getCompanyId(), party.getName(), party.getDocument().value(),
                party.getEmail().value(), party.getType());
    }
}
