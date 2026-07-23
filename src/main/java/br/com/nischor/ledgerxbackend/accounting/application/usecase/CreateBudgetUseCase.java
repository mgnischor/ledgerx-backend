package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.YearMonth;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;

    public CreateBudgetUseCase(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
            BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.budgetMapper = budgetMapper;
    }

    public BudgetDto execute(UUID companyId, UUID categoryId, YearMonth period, Money limit) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryId));

        if (category.getType() != TransactionType.EXPENSE) {
            throw new BusinessRuleViolationException(
                    "Budgets can only be set for EXPENSE categories, '%s' is a %s category"
                            .formatted(category.getName(), category.getType()));
        }

        if (budgetRepository.findByCompanyIdAndCategoryIdAndPeriod(companyId, categoryId, period).isPresent()) {
            throw new BusinessRuleViolationException(
                    "A budget for category '%s' already exists for %s".formatted(category.getName(), period));
        }

        var budget = new Budget(UUID.randomUUID(), companyId, categoryId, period, limit);
        return budgetMapper.toDto(budgetRepository.save(budget));
    }
}
