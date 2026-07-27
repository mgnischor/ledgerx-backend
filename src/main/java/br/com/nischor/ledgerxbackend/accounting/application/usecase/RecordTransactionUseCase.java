package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.TransactionMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.event.TransactionRecordedEvent;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.service.AccountBalanceService;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records a new financial transaction against an account and category, applying the corresponding balance
 * update and publishing a {@link TransactionRecordedEvent} once the transaction is persisted.
 */
@Service
public class RecordTransactionUseCase {

    private final FinancialAccountRepository financialAccountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AccountBalanceService accountBalanceService;
    private final TransactionMapper transactionMapper;
    private final DomainEventPublisher eventPublisher;

    /**
     * Creates the use case.
     *
     * @param financialAccountRepository repository used to look up and persist the financial account
     * @param categoryRepository repository used to look up the target category
     * @param transactionRepository repository used to persist the transaction
     * @param accountBalanceService domain service used to apply the transaction's effect on the account balance
     * @param transactionMapper mapper used to convert the saved transaction into a DTO
     * @param eventPublisher publisher used to notify other components that a transaction was recorded
     */
    public RecordTransactionUseCase(FinancialAccountRepository financialAccountRepository,
            CategoryRepository categoryRepository, TransactionRepository transactionRepository,
            AccountBalanceService accountBalanceService, TransactionMapper transactionMapper,
            DomainEventPublisher eventPublisher) {
        this.financialAccountRepository = financialAccountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.accountBalanceService = accountBalanceService;
        this.transactionMapper = transactionMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Records a transaction, updates the related account balance, and publishes a
     * {@link TransactionRecordedEvent}.
     *
     * @param financialAccountId the identifier of the financial account the transaction is posted to
     * @param categoryId the identifier of the category the transaction belongs to
     * @param type the transaction type (income or expense)
     * @param amount the transaction amount
     * @param description a free-text description of the transaction
     * @param occurredOn the date the transaction occurred
     * @return the recorded transaction as a DTO
     * @throws EntityNotFoundException if no financial account or category exists with the given identifiers
     * @throws BusinessRuleViolationException if the category's type does not match the transaction's type
     * @throws br.com.nischor.ledgerxbackend.accounting.domain.exception.InsufficientBalanceException if the
     *         account does not have enough balance for an expense transaction
     */
    @Transactional
    public TransactionDto execute(UUID financialAccountId, UUID categoryId, TransactionType type, Money amount,
            String description, LocalDate occurredOn) {
        var account = financialAccountRepository.findById(financialAccountId)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, financialAccountId));

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryId));

        if (category.getType() != type) {
            throw new BusinessRuleViolationException(
                    "Category '%s' is a %s category and cannot be used for a %s transaction"
                            .formatted(category.getName(), category.getType(), type));
        }

        accountBalanceService.apply(account, type, amount);
        financialAccountRepository.save(account);

        var transaction = new Transaction(UUID.randomUUID(), financialAccountId, categoryId, type, amount,
                description, occurredOn);
        var saved = transactionRepository.save(transaction);

        eventPublisher.publish(new TransactionRecordedEvent(saved.getId(), financialAccountId, type,
                amount.amount()));

        return transactionMapper.toDto(saved);
    }
}
