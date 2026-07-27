package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Budget} domain objects into {@link BudgetDto} instances for use by the application layer.
 */
@Component
public class BudgetMapper {

    /**
     * Converts a {@link Budget} domain object into its DTO representation.
     *
     * @param budget the domain budget to convert
     * @return the resulting {@link BudgetDto}
     */
    public BudgetDto toDto(Budget budget) {
        return new BudgetDto(budget.getId(), budget.getCompanyId(), budget.getCategoryId(), budget.getPeriod(),
                budget.getLimit().amount(), budget.getLimit().currency().getCurrencyCode(), budget.isActive());
    }
}
