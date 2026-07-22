package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.CategoryDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getCompanyId(), category.getName(), category.getType());
    }
}
