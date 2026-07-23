package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeactivateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    public DeactivateBudgetUseCase(BudgetRepository budgetRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
    }

    public BudgetDto execute(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException(Budget.class, budgetId));
        budget.deactivate();
        return budgetMapper.toDto(budgetRepository.save(budget));
    }
}
