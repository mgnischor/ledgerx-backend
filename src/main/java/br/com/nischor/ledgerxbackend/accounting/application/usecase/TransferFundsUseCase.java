package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transfers funds between two financial accounts by debiting the source account and crediting the destination
 * account.
 */
@Service
public class TransferFundsUseCase {

    private final FinancialAccountRepository financialAccountRepository;

    /**
     * Creates the use case.
     *
     * @param financialAccountRepository repository used to look up and persist the involved accounts
     */
    public TransferFundsUseCase(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

    /**
     * Transfers the given amount from one financial account to another.
     *
     * @param fromAccountId the identifier of the account funds are debited from
     * @param toAccountId the identifier of the account funds are credited to
     * @param amount the amount to transfer
     * @throws EntityNotFoundException if either account does not exist
     * @throws br.com.nischor.ledgerxbackend.accounting.domain.exception.InsufficientBalanceException if the
     *         source account does not have enough balance to cover the transfer
     */
    @Transactional
    public void execute(UUID fromAccountId, UUID toAccountId, Money amount) {
        var source = financialAccountRepository.findById(fromAccountId)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, fromAccountId));
        var destination = financialAccountRepository.findById(toAccountId)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, toAccountId));

        source.debit(amount);
        destination.credit(amount);

        financialAccountRepository.save(source);
        financialAccountRepository.save(destination);
    }
}
