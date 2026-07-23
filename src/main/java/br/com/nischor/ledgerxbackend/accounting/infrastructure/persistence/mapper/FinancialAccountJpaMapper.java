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
        var account = new FinancialAccount(entity.getId(), entity.getCompanyId(), entity.getName(), balance);
        if (!entity.isActive()) {
            account.deactivate();
        }
        return account;
    }

    public FinancialAccountJpaEntity toEntity(FinancialAccount account) {
        var entity = new FinancialAccountJpaEntity(account.getId(), account.getCompanyId(), account.getName(),
                account.getBalance().amount(), account.getBalance().currency().getCurrencyCode());
        entity.setActive(account.isActive());
        return entity;
    }
}
