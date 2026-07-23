package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionRuleMapper {

    public RecurringTransactionRuleDto toDto(RecurringTransactionRule rule) {
        return new RecurringTransactionRuleDto(rule.getId(), rule.getCompanyId(), rule.getFinancialAccountId(),
                rule.getCategoryId(), rule.getType(), rule.getAmount().amount(), rule.getDescription(),
                rule.getFrequency(), rule.getNextOccurrence(), rule.isActive());
    }
}
