package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.BudgetJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.YearMonth;
import java.util.Currency;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Budget} domain objects and {@link BudgetJpaEntity} persistence entities.
 */
@Component
public class BudgetJpaMapper {

    /**
     * Converts a persistence entity into its domain representation.
     *
     * @param entity the JPA entity to convert
     * @return the resulting {@link Budget}, deactivated if the entity is not active
     */
    public Budget toDomain(BudgetJpaEntity entity) {
        var limit = new Money(entity.getLimitAmount(), Currency.getInstance(entity.getCurrencyCode()));
        var period = YearMonth.of(entity.getPeriodYear(), entity.getPeriodMonth());
        var budget = new Budget(entity.getId(), entity.getCompanyId(), entity.getCategoryId(), period, limit);
        if (!entity.isActive()) {
            budget.deactivate();
        }
        return budget;
    }

    /**
     * Converts a domain object into its persistence representation.
     *
     * @param budget the domain budget to convert
     * @return the resulting {@link BudgetJpaEntity}
     */
    public BudgetJpaEntity toEntity(Budget budget) {
        var entity = new BudgetJpaEntity(budget.getId(), budget.getCompanyId(), budget.getCategoryId(),
                budget.getPeriod().getYear(), budget.getPeriod().getMonthValue(), budget.getLimit().amount(),
                budget.getLimit().currency().getCurrencyCode());
        entity.setActive(budget.isActive());
        return entity;
    }
}
