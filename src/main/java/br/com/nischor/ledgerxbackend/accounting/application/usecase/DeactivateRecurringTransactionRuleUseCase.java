package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Deactivates an existing recurring transaction rule, stopping further automatic transaction generation.
 */
@Service
public class DeactivateRecurringTransactionRuleUseCase {

    private final RecurringTransactionRuleRepository recurringTransactionRuleRepository;
    private final RecurringTransactionRuleMapper mapper;

    /**
     * Creates the use case.
     *
     * @param recurringTransactionRuleRepository repository used to look up and persist the rule
     * @param mapper mapper used to convert the saved rule into a DTO
     */
    public DeactivateRecurringTransactionRuleUseCase(
            RecurringTransactionRuleRepository recurringTransactionRuleRepository,
            RecurringTransactionRuleMapper mapper) {
        this.recurringTransactionRuleRepository = recurringTransactionRuleRepository;
        this.mapper = mapper;
    }

    /**
     * Deactivates the recurring transaction rule with the given identifier.
     *
     * @param ruleId the identifier of the rule to deactivate
     * @return the deactivated rule as a DTO
     * @throws EntityNotFoundException if no rule exists with the given identifier
     */
    public RecurringTransactionRuleDto execute(UUID ruleId) {
        var rule = recurringTransactionRuleRepository.findById(ruleId)
                .orElseThrow(() -> new EntityNotFoundException(RecurringTransactionRule.class, ruleId));
        rule.deactivate();
        return mapper.toDto(recurringTransactionRuleRepository.save(rule));
    }
}
