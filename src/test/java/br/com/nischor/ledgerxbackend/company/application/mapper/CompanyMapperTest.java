package br.com.nischor.ledgerxbackend.company.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CompanyMapperTest {

    private final CompanyMapper mapper = new CompanyMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var company = new Company(UUID.randomUUID(), "Acme Ltda", "Acme", DocumentNumber.cnpj("11222333000181"),
                CompanySize.MICRO, new Address("Main St", "100", "Sao Paulo", "SP", "01310-100", "Brazil"));

        var dto = mapper.toDto(company);

        assertThat(dto.id()).isEqualTo(company.getId());
        assertThat(dto.legalName()).isEqualTo("Acme Ltda");
        assertThat(dto.tradeName()).isEqualTo("Acme");
        assertThat(dto.cnpj()).isEqualTo("11222333000181");
        assertThat(dto.size()).isEqualTo(CompanySize.MICRO);
        assertThat(dto.active()).isTrue();
    }
}
