package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeactivateRecurringTransactionRuleUseCase {

    private final RecurringTransactionRuleRepository recurringTransactionRuleRepository;
    private final RecurringTransactionRuleMapper mapper;

    public DeactivateRecurringTransactionRuleUseCase(
            RecurringTransactionRuleRepository recurringTransactionRuleRepository,
            RecurringTransactionRuleMapper mapper) {
        this.recurringTransactionRuleRepository = recurringTransactionRuleRepository;
        this.mapper = mapper;
    }

    public RecurringTransactionRuleDto execute(UUID ruleId) {
        var rule = recurringTransactionRuleRepository.findById(ruleId)
                .orElseThrow(() -> new EntityNotFoundException(RecurringTransactionRule.class, ruleId));
        rule.deactivate();
        return mapper.toDto(recurringTransactionRuleRepository.save(rule));
    }
}
