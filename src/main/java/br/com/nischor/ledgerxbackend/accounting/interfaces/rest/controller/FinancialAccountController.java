package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/financial-accounts")
public class FinancialAccountController {

    private final FinancialAccountRepository financialAccountRepository;

    public FinancialAccountController(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

    @GetMapping
    public List<?> listByCompany(@PathVariable UUID companyId) {
        return financialAccountRepository.findAllByCompanyId(companyId);
    }
}
