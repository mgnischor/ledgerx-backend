package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Deactivates an existing budget.
 */
@Service
public class DeactivateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    /**
     * Creates the use case.
     *
     * @param budgetRepository repository used to look up and persist the budget
     * @param budgetMapper mapper used to convert the saved budget into a DTO
     */
    public DeactivateBudgetUseCase(BudgetRepository budgetRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
    }

    /**
     * Deactivates the budget with the given identifier.
     *
     * @param budgetId the identifier of the budget to deactivate
     * @return the deactivated budget as a DTO
     * @throws EntityNotFoundException if no budget exists with the given identifier
     */
    public BudgetDto execute(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException(Budget.class, budgetId));
        budget.deactivate();
        return budgetMapper.toDto(budgetRepository.save(budget));
    }
}
