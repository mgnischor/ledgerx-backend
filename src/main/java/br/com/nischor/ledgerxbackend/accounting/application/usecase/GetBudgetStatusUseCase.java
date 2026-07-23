package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetStatusDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetBudgetStatusUseCase {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public GetBudgetStatusUseCase(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public BudgetStatusDto execute(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException(Budget.class, budgetId));

        var period = budget.getPeriod();
        var transactions = transactionRepository.findByCategoryIdAndPeriod(budget.getCategoryId(),
                period.atDay(1), period.atEndOfMonth());

        var spent = transactions.stream()
                .map(transaction -> transaction.getAmount())
                .reduce(Money.zero(budget.getLimit().currency()), Money::add);

        var remaining = budget.getLimit().subtract(spent);

        return new BudgetStatusDto(budget.getId(), budget.getCategoryId(), period, budget.getLimit().amount(),
                spent.amount(), remaining.amount(), remaining.isNegative());
    }
}
