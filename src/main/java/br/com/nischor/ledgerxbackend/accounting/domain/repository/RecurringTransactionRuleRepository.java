package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurringTransactionRule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringTransactionRuleRepository {

    RecurringTransactionRule save(RecurringTransactionRule rule);

    Optional<RecurringTransactionRule> findById(UUID id);

    List<RecurringTransactionRule> findAllByCompanyId(UUID companyId);

    List<RecurringTransactionRule> findAllByCompanyIdAndActiveTrue(UUID companyId);
}
