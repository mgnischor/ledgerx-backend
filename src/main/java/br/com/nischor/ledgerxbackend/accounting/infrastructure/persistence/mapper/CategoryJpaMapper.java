package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryJpaMapper {

    public Category toDomain(CategoryJpaEntity entity) {
        return new Category(entity.getId(), entity.getCompanyId(), entity.getName(), entity.getType());
    }

    public CategoryJpaEntity toEntity(Category category) {
        return new CategoryJpaEntity(category.getId(), category.getCompanyId(), category.getName(),
                category.getType());
    }
}
