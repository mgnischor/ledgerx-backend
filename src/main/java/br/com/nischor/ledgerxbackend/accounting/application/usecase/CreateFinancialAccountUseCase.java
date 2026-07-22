package br.com.nischor.ledgerxbackend.accounting.application.usecase;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateFinancialAccountUseCase {

    private final FinancialAccountRepository financialAccountRepository;
    private final FinancialAccountMapper financialAccountMapper;

    public CreateFinancialAccountUseCase(FinancialAccountRepository financialAccountRepository,
            FinancialAccountMapper financialAccountMapper) {
        this.financialAccountRepository = financialAccountRepository;
        this.financialAccountMapper = financialAccountMapper;
    }

    public FinancialAccountDto execute(UUID companyId, String name, Money openingBalance) {
        var account = new FinancialAccount(UUID.randomUUID(), companyId, name, openingBalance);
        return financialAccountMapper.toDto(financialAccountRepository.save(account));
    }
}
