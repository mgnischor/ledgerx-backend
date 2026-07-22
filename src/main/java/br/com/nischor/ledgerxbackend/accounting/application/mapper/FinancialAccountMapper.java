package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import org.springframework.stereotype.Component;

@Component
public class FinancialAccountMapper {

    public FinancialAccountDto toDto(FinancialAccount account) {
        return new FinancialAccountDto(account.getId(), account.getCompanyId(), account.getName(),
                account.getBalance().amount(), account.getBalance().currency().getCurrencyCode(),
                account.isActive());
    }
}
