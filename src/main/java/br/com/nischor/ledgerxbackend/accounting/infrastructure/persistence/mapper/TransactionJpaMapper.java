package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.TransactionJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Transaction} domain objects and {@link TransactionJpaEntity} persistence entities.
 */
@Component
public class TransactionJpaMapper {

    /**
     * Converts a persistence entity into its domain representation. The amount currency is currently fixed to
     * {@code BRL} since {@link TransactionJpaEntity} does not store a currency code.
     *
     * @param entity the JPA entity to convert
     * @return the resulting {@link Transaction}
     */
    public Transaction toDomain(TransactionJpaEntity entity) {
        var amount = new Money(entity.getAmount(), Currency.getInstance("BRL"));
        return new Transaction(entity.getId(), entity.getFinancialAccountId(), entity.getCategoryId(),
                entity.getType(), amount, entity.getDescription(), entity.getOccurredOn());
    }

    /**
     * Converts a domain object into its persistence representation.
     *
     * @param transaction the domain transaction to convert
     * @return the resulting {@link TransactionJpaEntity}
     */
    public TransactionJpaEntity toEntity(Transaction transaction) {
        return new TransactionJpaEntity(transaction.getId(), transaction.getFinancialAccountId(),
                transaction.getCategoryId(), transaction.getType(), transaction.getAmount().amount(),
                transaction.getDescription(), transaction.getOccurredOn());
    }
}
