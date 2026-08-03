package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RecurringTransactionRuleMapperTest {

    private final RecurringTransactionRuleMapper mapper = new RecurringTransactionRuleMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var rule = new RecurringTransactionRule(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), TransactionType.EXPENSE, Money.brl(new BigDecimal("120.00")), "Rent",
                RecurrenceFrequency.MONTHLY, LocalDate.now());

        var dto = mapper.toDto(rule);

        assertThat(dto.id()).isEqualTo(rule.getId());
        assertThat(dto.companyId()).isEqualTo(rule.getCompanyId());
        assertThat(dto.financialAccountId()).isEqualTo(rule.getFinancialAccountId());
        assertThat(dto.categoryId()).isEqualTo(rule.getCategoryId());
        assertThat(dto.type()).isEqualTo(TransactionType.EXPENSE);
        assertThat(dto.amount()).isEqualByComparingTo("120.00");
        assertThat(dto.description()).isEqualTo("Rent");
        assertThat(dto.frequency()).isEqualTo(RecurrenceFrequency.MONTHLY);
        assertThat(dto.nextOccurrence()).isEqualTo(rule.getNextOccurrence());
        assertThat(dto.active()).isTrue();
    }
}
