package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Category} domain objects and {@link CategoryJpaEntity} persistence entities.
 */
@Component
public class CategoryJpaMapper {

    /**
     * Converts a persistence entity into its domain representation.
     *
     * @param entity the JPA entity to convert
     * @return the resulting {@link Category}
     */
    public Category toDomain(CategoryJpaEntity entity) {
        return new Category(entity.getId(), entity.getCompanyId(), entity.getName(), entity.getType());
    }

    /**
     * Converts a domain object into its persistence representation.
     *
     * @param category the domain category to convert
     * @return the resulting {@link CategoryJpaEntity}
     */
    public CategoryJpaEntity toEntity(Category category) {
        return new CategoryJpaEntity(category.getId(), category.getCompanyId(), category.getName(),
                category.getType());
    }
}
