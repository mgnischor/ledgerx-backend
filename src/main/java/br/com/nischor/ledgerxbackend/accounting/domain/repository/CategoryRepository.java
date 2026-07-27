package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for {@link Category} aggregates.
 */
public interface CategoryRepository {

    /**
     * Persists a category.
     *
     * @param category the category to save
     * @return the saved category
     */
    Category save(Category category);

    /**
     * Finds a category by its identifier.
     *
     * @param id the category identifier
     * @return an {@link Optional} containing the category if found, or empty otherwise
     */
    Optional<Category> findById(UUID id);

    /**
     * Finds all categories belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of categories owned by the company
     */
    List<Category> findAllByCompanyId(UUID companyId);
}
