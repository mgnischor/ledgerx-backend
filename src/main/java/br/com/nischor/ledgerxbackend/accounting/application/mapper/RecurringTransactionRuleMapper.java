package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import org.springframework.stereotype.Component;

/**
 * Converts {@link RecurringTransactionRule} domain objects into {@link RecurringTransactionRuleDto} instances for
 * use by the application layer.
 */
@Component
public class RecurringTransactionRuleMapper {

    /**
     * Converts a {@link RecurringTransactionRule} domain object into its DTO representation.
     *
     * @param rule the domain recurring transaction rule to convert
     * @return the resulting {@link RecurringTransactionRuleDto}
     */
    public RecurringTransactionRuleDto toDto(RecurringTransactionRule rule) {
        return new RecurringTransactionRuleDto(rule.getId(), rule.getCompanyId(), rule.getFinancialAccountId(),
                rule.getCategoryId(), rule.getType(), rule.getAmount().amount(), rule.getDescription(),
                rule.getFrequency(), rule.getNextOccurrence(), rule.isActive());
    }
}
