package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.PartyJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link Party} domain object and its {@link PartyJpaEntity} persistence
 * representation.
 */
@Component
public class PartyJpaMapper {

    /**
     * Converts a JPA party entity into a domain party, inferring the document type (CPF or CNPJ)
     * from the length of the stored document number.
     *
     * @param entity the JPA party entity to convert
     * @return the corresponding domain party
     */
    public Party toDomain(PartyJpaEntity entity) {
        var document = entity.getDocument().length() == 11
                ? DocumentNumber.cpf(entity.getDocument())
                : DocumentNumber.cnpj(entity.getDocument());
        return new Party(entity.getId(), entity.getCompanyId(), entity.getName(), document,
                new EmailAddress(entity.getEmail()), entity.getType());
    }

    /**
     * Converts a domain party into a JPA entity ready for persistence.
     *
     * @param party the domain party to convert
     * @return the corresponding JPA party entity
     */
    public PartyJpaEntity toEntity(Party party) {
        return new PartyJpaEntity(party.getId(), party.getCompanyId(), party.getName(), party.getDocument().value(),
                party.getEmail().value(), party.getType());
    }
}
