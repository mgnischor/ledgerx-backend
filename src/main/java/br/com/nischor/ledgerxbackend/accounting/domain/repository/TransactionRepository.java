package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByFinancialAccountIdAndPeriod(UUID financialAccountId, LocalDate from, LocalDate to);

    List<Transaction> findByCategoryIdAndPeriod(UUID categoryId, LocalDate from, LocalDate to);
}
