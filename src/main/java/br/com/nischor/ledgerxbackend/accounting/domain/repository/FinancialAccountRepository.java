package br.com.nischor.ledgerxbackend.accounting.domain.repository;

import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialAccountRepository {

    FinancialAccount save(FinancialAccount account);

    Optional<FinancialAccount> findById(UUID id);

    List<FinancialAccount> findAllByCompanyId(UUID companyId);
}
