package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Materializes every {@code RecurringTransactionRule} that is due (BR-113) into a real
 * {@code Transaction}, delegating to {@link RecordTransactionUseCase} so the same validation and
 * balance-update logic used for manually recorded transactions applies here too. Each processed
 * rule is then advanced to its next occurrence per its {@code frequency}.
 */
@Service
public class GenerateDueRecurringTransactionsUseCase {

    private final RecurringTransactionRuleRepository recurringTransactionRuleRepository;
    private final RecordTransactionUseCase recordTransactionUseCase;

    public GenerateDueRecurringTransactionsUseCase(
            RecurringTransactionRuleRepository recurringTransactionRuleRepository,
            RecordTransactionUseCase recordTransactionUseCase) {
        this.recurringTransactionRuleRepository = recurringTransactionRuleRepository;
        this.recordTransactionUseCase = recordTransactionUseCase;
    }

    public List<TransactionDto> execute(UUID companyId) {
        var today = LocalDate.now();
        var dueRules = recurringTransactionRuleRepository.findAllByCompanyIdAndActiveTrue(companyId).stream()
                .filter(rule -> rule.isDue(today))
                .toList();

        return dueRules.stream().map(rule -> {
            var transaction = recordTransactionUseCase.execute(rule.getFinancialAccountId(), rule.getCategoryId(),
                    rule.getType(), rule.getAmount(), rule.getDescription(), rule.getNextOccurrence());
            rule.advance();
            recurringTransactionRuleRepository.save(rule);
            return transaction;
        }).toList();
    }
}
