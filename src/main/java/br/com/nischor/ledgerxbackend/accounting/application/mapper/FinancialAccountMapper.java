package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import org.springframework.stereotype.Component;

/**
 * Converts {@link FinancialAccount} domain objects into {@link FinancialAccountDto} instances for use by the
 * application layer.
 */
@Component
public class FinancialAccountMapper {

    /**
     * Converts a {@link FinancialAccount} domain object into its DTO representation.
     *
     * @param account the domain financial account to convert
     * @return the resulting {@link FinancialAccountDto}
     */
    public FinancialAccountDto toDto(FinancialAccount account) {
        return new FinancialAccountDto(account.getId(), account.getCompanyId(), account.getName(),
                account.getBalance().amount(), account.getBalance().currency().getCurrencyCode(),
                account.isActive());
    }
}
