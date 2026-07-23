package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.RecurringTransactionRuleJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionRuleJpaMapper {

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

    public RecurringTransactionRuleJpaEntity toEntity(RecurringTransactionRule rule) {
        var entity = new RecurringTransactionRuleJpaEntity(rule.getId(), rule.getCompanyId(),
                rule.getFinancialAccountId(), rule.getCategoryId(), rule.getType(), rule.getAmount().amount(),
                rule.getAmount().currency().getCurrencyCode(), rule.getDescription(), rule.getFrequency(),
                rule.getNextOccurrence());
        entity.setActive(rule.isActive());
        return entity;
    }
}
