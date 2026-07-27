package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.FinancialAccountJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link FinancialAccount} domain objects and {@link FinancialAccountJpaEntity} persistence
 * entities.
 */
@Component
public class FinancialAccountJpaMapper {

    /**
     * Converts a persistence entity into its domain representation.
     *
     * @param entity the JPA entity to convert
     * @return the resulting {@link FinancialAccount}, deactivated if the entity is not active
     */
    public FinancialAccount toDomain(FinancialAccountJpaEntity entity) {
        var balance = new Money(entity.getBalance(), Currency.getInstance(entity.getCurrencyCode()));
        var account = new FinancialAccount(entity.getId(), entity.getCompanyId(), entity.getName(), balance);
        if (!entity.isActive()) {
            account.deactivate();
        }
        return account;
    }

    /**
     * Converts a domain object into its persistence representation.
     *
     * @param account the domain financial account to convert
     * @return the resulting {@link FinancialAccountJpaEntity}
     */
    public FinancialAccountJpaEntity toEntity(FinancialAccount account) {
        var entity = new FinancialAccountJpaEntity(account.getId(), account.getCompanyId(), account.getName(),
                account.getBalance().amount(), account.getBalance().currency().getCurrencyCode());
        entity.setActive(account.isActive());
        return entity;
    }
}
