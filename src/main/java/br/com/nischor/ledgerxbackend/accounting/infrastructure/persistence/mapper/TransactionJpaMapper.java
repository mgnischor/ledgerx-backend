package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.TransactionJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class TransactionJpaMapper {

    public Transaction toDomain(TransactionJpaEntity entity) {
        var amount = new Money(entity.getAmount(), Currency.getInstance("BRL"));
        return new Transaction(entity.getId(), entity.getFinancialAccountId(), entity.getCategoryId(),
                entity.getType(), amount, entity.getDescription(), entity.getOccurredOn());
    }

    public TransactionJpaEntity toEntity(Transaction transaction) {
        return new TransactionJpaEntity(transaction.getFinancialAccountId(), transaction.getCategoryId(),
                transaction.getType(), transaction.getAmount().amount(), transaction.getDescription(),
                transaction.getOccurredOn());
    }
}
