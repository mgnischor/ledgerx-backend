package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BudgetMapperTest {

    private final BudgetMapper mapper = new BudgetMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), YearMonth.now(),
                Money.brl(new BigDecimal("500.00")));

        var dto = mapper.toDto(budget);

        assertThat(dto.id()).isEqualTo(budget.getId());
        assertThat(dto.companyId()).isEqualTo(budget.getCompanyId());
        assertThat(dto.categoryId()).isEqualTo(budget.getCategoryId());
        assertThat(dto.period()).isEqualTo(budget.getPeriod());
        assertThat(dto.limit()).isEqualByComparingTo("500.00");
        assertThat(dto.currency()).isEqualTo("BRL");
        assertThat(dto.active()).isTrue();
    }

    @Test
    void reflectsInactiveBudget() {
        var budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), YearMonth.now(),
                Money.brl(new BigDecimal("500.00")));
        budget.deactivate();

        assertThat(mapper.toDto(budget).active()).isFalse();
    }
}
