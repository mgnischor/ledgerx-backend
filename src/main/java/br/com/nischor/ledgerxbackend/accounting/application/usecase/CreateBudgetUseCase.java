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

/**
 * Creates a new budget for a category and monthly period, ensuring the category is an expense category and that
 * no duplicate budget already exists for the same period.
 */
@Service
public class CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;

    /**
     * Creates the use case.
     *
     * @param budgetRepository repository used to persist and look up budgets
     * @param categoryRepository repository used to look up the target category
     * @param budgetMapper mapper used to convert the saved budget into a DTO
     */
    public CreateBudgetUseCase(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
            BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.budgetMapper = budgetMapper;
    }

    /**
     * Creates a budget limiting spending on a category for a given period.
     *
     * @param companyId the identifier of the company the budget belongs to
     * @param categoryId the identifier of the category the budget applies to
     * @param period the year and month the budget covers
     * @param limit the maximum amount allowed to be spent in the period
     * @return the created budget as a DTO
     * @throws EntityNotFoundException if no category exists with the given identifier
     * @throws BusinessRuleViolationException if the category is not an expense category, or a budget for the same
     *         category and period already exists
     */
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
