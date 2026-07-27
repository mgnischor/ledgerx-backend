package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Transaction} domain objects into {@link TransactionDto} instances for use by the application
 * layer.
 */
@Component
public class TransactionMapper {

    /**
     * Converts a {@link Transaction} domain object into its DTO representation.
     *
     * @param transaction the domain transaction to convert
     * @return the resulting {@link TransactionDto}
     */
    public TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(transaction.getId(), transaction.getFinancialAccountId(),
                transaction.getCategoryId(), transaction.getType(), transaction.getAmount().amount(),
                transaction.getDescription(), transaction.getOccurredOn());
    }
}
