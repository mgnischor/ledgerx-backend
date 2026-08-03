package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {

    private final CategoryMapper mapper = new CategoryMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var category = new Category(UUID.randomUUID(), UUID.randomUUID(), "Groceries", TransactionType.EXPENSE);

        var dto = mapper.toDto(category);

        assertThat(dto.id()).isEqualTo(category.getId());
        assertThat(dto.companyId()).isEqualTo(category.getCompanyId());
        assertThat(dto.name()).isEqualTo("Groceries");
        assertThat(dto.type()).isEqualTo(TransactionType.EXPENSE);
    }
}
