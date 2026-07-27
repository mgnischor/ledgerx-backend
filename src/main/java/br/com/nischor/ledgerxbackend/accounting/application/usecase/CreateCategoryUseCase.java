package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.CategoryDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.CategoryMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Creates a new transaction category for a company.
 */
@Service
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Creates the use case.
     *
     * @param categoryRepository repository used to persist the category
     * @param categoryMapper mapper used to convert the saved category into a DTO
     */
    public CreateCategoryUseCase(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Creates a new category.
     *
     * @param companyId the identifier of the company the category belongs to
     * @param name the category name
     * @param type the transaction type (income or expense) the category is associated with
     * @return the created category as a DTO
     */
    public CategoryDto execute(UUID companyId, String name, TransactionType type) {
        var category = new Category(UUID.randomUUID(), companyId, name, type);
        return categoryMapper.toDto(categoryRepository.save(category));
    }
}
