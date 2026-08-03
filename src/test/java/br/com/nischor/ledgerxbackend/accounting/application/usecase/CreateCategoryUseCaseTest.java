package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.accounting.application.dto.CategoryDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.CategoryMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private CreateCategoryUseCase useCase;

    private final UUID companyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CreateCategoryUseCase(categoryRepository, categoryMapper);
    }

    @Test
    void createsAndPersistsCategory() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new CategoryDto(UUID.randomUUID(), companyId, "Groceries", TransactionType.EXPENSE);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(dto);

        var result = useCase.execute(companyId, "Groceries", TransactionType.EXPENSE);

        assertThat(result).isEqualTo(dto);
    }
}
