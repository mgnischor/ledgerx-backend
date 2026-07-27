package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.RecurringTransactionRuleJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link RecurringTransactionRule} domain objects and {@link RecurringTransactionRuleJpaEntity}
 * persistence entities.
 */
@Component
public class RecurringTransactionRuleJpaMapper {

    /**
     * Converts a persistence entity into its domain representation.
     *
     * @param entity the JPA entity to convert
     * @return the resulting {@link RecurringTransactionRule}, deactivated if the entity is not active
     */
    public RecurringTransactionRule toDomain(RecurringTransactionRuleJpaEntity entity) {
        var amount = new Money(entity.getAmount(), Currency.getInstance(entity.getCurrencyCode()));
        var rule = new RecurringTransactionRule(entity.getId(), entity.getCompanyId(),
                entity.getFinancialAccountId(), entity.getCategoryId(), entity.getType(), amount,
                entity.getDescription(), entity.getFrequency(), entity.getNextOccurrence());
        if (!entity.isActive()) {
            rule.deactivate();
        }
        return rule;
    }

    /**
     * Converts a domain object into its persistence representation.
     *
     * @param rule the domain recurring transaction rule to convert
     * @return the resulting {@link RecurringTransactionRuleJpaEntity}
     */
    public RecurringTransactionRuleJpaEntity toEntity(RecurringTransactionRule rule) {
        var entity = new RecurringTransactionRuleJpaEntity(rule.getId(), rule.getCompanyId(),
                rule.getFinancialAccountId(), rule.getCategoryId(), rule.getType(), rule.getAmount().amount(),
                rule.getAmount().currency().getCurrencyCode(), rule.getDescription(), rule.getFrequency(),
                rule.getNextOccurrence());
        entity.setActive(rule.isActive());
        return entity;
    }
}
