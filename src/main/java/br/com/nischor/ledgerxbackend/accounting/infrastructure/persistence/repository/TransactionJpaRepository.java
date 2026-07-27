package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity.TransactionJpaEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link TransactionJpaEntity}.
 */
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    /**
     * Finds transaction entities posted to a financial account within a date range.
     *
     * @param financialAccountId the identifier of the financial account
     * @param from the inclusive start date of the range
     * @param to the inclusive end date of the range
     * @return the list of matching transaction entities
     */
    List<TransactionJpaEntity> findAllByFinancialAccountIdAndOccurredOnBetween(UUID financialAccountId,
            LocalDate from, LocalDate to);

    /**
     * Finds transaction entities belonging to a category within a date range.
     *
     * @param categoryId the identifier of the category
     * @param from the inclusive start date of the range
     * @param to the inclusive end date of the range
     * @return the list of matching transaction entities
     */
    List<TransactionJpaEntity> findAllByCategoryIdAndOccurredOnBetween(UUID categoryId, LocalDate from,
            LocalDate to);
}
