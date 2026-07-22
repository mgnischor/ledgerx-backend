package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferFundsUseCase {

    private final FinancialAccountRepository financialAccountRepository;

    public TransferFundsUseCase(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

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
