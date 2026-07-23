package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {

    Budget save(Budget budget);

    Optional<Budget> findById(UUID id);

    List<Budget> findAllByCompanyId(UUID companyId);

    Optional<Budget> findByCompanyIdAndCategoryIdAndPeriod(UUID companyId, UUID categoryId, YearMonth period);
}
