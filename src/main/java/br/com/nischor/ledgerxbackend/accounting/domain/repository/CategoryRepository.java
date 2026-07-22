package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    List<Category> findAllByCompanyId(UUID companyId);
}
