package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FinancialAccountMapperTest {

    private final FinancialAccountMapper mapper = new FinancialAccountMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var account = new FinancialAccount(UUID.randomUUID(), UUID.randomUUID(), "Checking",
                Money.brl(new BigDecimal("250.50")));

        var dto = mapper.toDto(account);

        assertThat(dto.id()).isEqualTo(account.getId());
        assertThat(dto.companyId()).isEqualTo(account.getCompanyId());
        assertThat(dto.name()).isEqualTo("Checking");
        assertThat(dto.balance()).isEqualByComparingTo("250.50");
        assertThat(dto.currency()).isEqualTo("BRL");
        assertThat(dto.active()).isTrue();
    }
}
