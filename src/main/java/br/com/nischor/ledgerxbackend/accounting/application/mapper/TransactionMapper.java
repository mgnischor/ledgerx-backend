package br.com.nischor.ledgerxbackend.accounting.application.mapper;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(transaction.getId(), transaction.getFinancialAccountId(),
                transaction.getCategoryId(), transaction.getType(), transaction.getAmount().amount(),
                transaction.getDescription(), transaction.getOccurredOn());
    }
}
