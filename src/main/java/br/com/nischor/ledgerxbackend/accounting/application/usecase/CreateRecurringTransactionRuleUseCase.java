package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateRecurringTransactionRuleUseCase {

    private final RecurringTransactionRuleRepository recurringTransactionRuleRepository;
    private final CategoryRepository categoryRepository;
    private final RecurringTransactionRuleMapper mapper;

    public CreateRecurringTransactionRuleUseCase(
            RecurringTransactionRuleRepository recurringTransactionRuleRepository,
            CategoryRepository categoryRepository, RecurringTransactionRuleMapper mapper) {
        this.recurringTransactionRuleRepository = recurringTransactionRuleRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public RecurringTransactionRuleDto execute(UUID companyId, UUID financialAccountId, UUID categoryId,
            TransactionType type, Money amount, String description, RecurrenceFrequency frequency,
            LocalDate firstOccurrence) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryId));

        if (category.getType() != type) {
            throw new BusinessRuleViolationException(
                    "Category '%s' is a %s category and cannot be used for a %s recurring rule"
                            .formatted(category.getName(), category.getType(), type));
        }

        var rule = new RecurringTransactionRule(UUID.randomUUID(), companyId, financialAccountId, categoryId, type,
                amount, description, frequency, firstOccurrence);
        return mapper.toDto(recurringTransactionRuleRepository.save(rule));
    }
}
