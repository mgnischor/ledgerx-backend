package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.INCOME, Money.brl(new BigDecimal("300.00")), "Salary", LocalDate.now());

        var dto = mapper.toDto(transaction);

        assertThat(dto.id()).isEqualTo(transaction.getId());
        assertThat(dto.financialAccountId()).isEqualTo(transaction.getFinancialAccountId());
        assertThat(dto.categoryId()).isEqualTo(transaction.getCategoryId());
        assertThat(dto.type()).isEqualTo(TransactionType.INCOME);
        assertThat(dto.amount()).isEqualByComparingTo("300.00");
        assertThat(dto.description()).isEqualTo("Salary");
        assertThat(dto.occurredOn()).isEqualTo(transaction.getOccurredOn());
    }
}
