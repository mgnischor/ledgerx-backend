package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Persistence port for {@link Transaction} aggregates.
 */
public interface TransactionRepository {

    /**
     * Persists a transaction.
     *
     * @param transaction the transaction to save
     * @return the saved transaction
     */
    Transaction save(Transaction transaction);

    /**
     * Finds transactions posted to a financial account within a date range.
     *
     * @param financialAccountId the identifier of the financial account
     * @param from the inclusive start date of the range
     * @param to the inclusive end date of the range
     * @return the list of matching transactions
     */
    List<Transaction> findByFinancialAccountIdAndPeriod(UUID financialAccountId, LocalDate from, LocalDate to);

    /**
     * Finds transactions belonging to a category within a date range.
     *
     * @param categoryId the identifier of the category
     * @param from the inclusive start date of the range
     * @param to the inclusive end date of the range
     * @return the list of matching transactions
     */
    List<Transaction> findByCategoryIdAndPeriod(UUID categoryId, LocalDate from, LocalDate to);
}
