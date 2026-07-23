package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.BudgetJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.YearMonth;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class BudgetJpaMapper {

    public Budget toDomain(BudgetJpaEntity entity) {
        var limit = new Money(entity.getLimitAmount(), Currency.getInstance(entity.getCurrencyCode()));
        var period = YearMonth.of(entity.getPeriodYear(), entity.getPeriodMonth());
        var budget = new Budget(entity.getId(), entity.getCompanyId(), entity.getCategoryId(), period, limit);
        if (!entity.isActive()) {
            budget.deactivate();
        }
        return budget;
    }

    public BudgetJpaEntity toEntity(Budget budget) {
        var entity = new BudgetJpaEntity(budget.getId(), budget.getCompanyId(), budget.getCategoryId(),
                budget.getPeriod().getYear(), budget.getPeriod().getMonthValue(), budget.getLimit().amount(),
                budget.getLimit().currency().getCurrencyCode());
        entity.setActive(budget.isActive());
        return entity;
    }
}
