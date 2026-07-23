package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.FinancialAccountJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class FinancialAccountJpaMapper {

    public FinancialAccount toDomain(FinancialAccountJpaEntity entity) {
        var balance = new Money(entity.getBalance(), Currency.getInstance(entity.getCurrencyCode()));
        return new FinancialAccount(entity.getId(), entity.getCompanyId(), entity.getName(), balance);
    }

    public FinancialAccountJpaEntity toEntity(FinancialAccount account) {
        return new FinancialAccountJpaEntity(account.getId(), account.getCompanyId(), account.getName(),
                account.getBalance().amount(), account.getBalance().currency().getCurrencyCode());
    }
}
