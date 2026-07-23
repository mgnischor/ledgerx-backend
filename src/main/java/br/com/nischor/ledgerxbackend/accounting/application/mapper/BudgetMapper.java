package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public BudgetDto toDto(Budget budget) {
        return new BudgetDto(budget.getId(), budget.getCompanyId(), budget.getCategoryId(), budget.getPeriod(),
                budget.getLimit().amount(), budget.getLimit().currency().getCurrencyCode(), budget.isActive());
    }
}
