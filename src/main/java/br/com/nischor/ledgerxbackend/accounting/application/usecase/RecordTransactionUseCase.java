package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.TransactionMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.event.TransactionRecordedEvent;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.service.AccountBalanceService;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordTransactionUseCase {

    private final FinancialAccountRepository financialAccountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountBalanceService accountBalanceService;
    private final TransactionMapper transactionMapper;
    private final DomainEventPublisher eventPublisher;

    public RecordTransactionUseCase(FinancialAccountRepository financialAccountRepository,
            TransactionRepository transactionRepository, AccountBalanceService accountBalanceService,
            TransactionMapper transactionMapper, DomainEventPublisher eventPublisher) {
        this.financialAccountRepository = financialAccountRepository;
        this.transactionRepository = transactionRepository;
        this.accountBalanceService = accountBalanceService;
        this.transactionMapper = transactionMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TransactionDto execute(UUID financialAccountId, UUID categoryId, TransactionType type, Money amount,
            String description, LocalDate occurredOn) {
        var account = financialAccountRepository.findById(financialAccountId)
                .orElseThrow(() -> new EntityNotFoundException(
                        br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount.class,
                        financialAccountId));

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
