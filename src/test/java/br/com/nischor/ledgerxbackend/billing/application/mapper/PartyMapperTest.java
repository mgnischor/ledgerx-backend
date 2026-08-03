package br.com.nischor.ledgerxbackend.billing.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PartyMapperTest {

    private final PartyMapper mapper = new PartyMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var party = new Party(UUID.randomUUID(), UUID.randomUUID(), "Jane Doe", DocumentNumber.cpf("11144477735"),
                new EmailAddress("jane@example.com"), PartyType.CUSTOMER);

        var dto = mapper.toDto(party);

        assertThat(dto.id()).isEqualTo(party.getId());
        assertThat(dto.companyId()).isEqualTo(party.getCompanyId());
        assertThat(dto.name()).isEqualTo("Jane Doe");
        assertThat(dto.document()).isEqualTo("11144477735");
        assertThat(dto.email()).isEqualTo("jane@example.com");
        assertThat(dto.type()).isEqualTo(PartyType.CUSTOMER);
    }
}
