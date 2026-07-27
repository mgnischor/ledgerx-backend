package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.CategoryDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Category} domain objects into {@link CategoryDto} instances for use by the application layer.
 */
@Component
public class CategoryMapper {

    /**
     * Converts a {@link Category} domain object into its DTO representation.
     *
     * @param category the domain category to convert
     * @return the resulting {@link CategoryDto}
     */
    public CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getCompanyId(), category.getName(), category.getType());
    }
}
