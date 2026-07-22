package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper.CategoryJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryJpaMapper mapper;

    public CategoryRepositoryAdapter(CategoryJpaRepository jpaRepository, CategoryJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Category save(Category category) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(category)));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Category> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findAllByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }
}
