package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateFinancialAccountUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.DeactivateFinancialAccountUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.CreateFinancialAccountRequest;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/financial-accounts")
public class FinancialAccountController {

    private final FinancialAccountRepository financialAccountRepository;
    private final FinancialAccountMapper financialAccountMapper;
    private final CreateFinancialAccountUseCase createFinancialAccountUseCase;
    private final DeactivateFinancialAccountUseCase deactivateFinancialAccountUseCase;

    public FinancialAccountController(FinancialAccountRepository financialAccountRepository,
            FinancialAccountMapper financialAccountMapper, CreateFinancialAccountUseCase createFinancialAccountUseCase,
            DeactivateFinancialAccountUseCase deactivateFinancialAccountUseCase) {
        this.financialAccountRepository = financialAccountRepository;
        this.financialAccountMapper = financialAccountMapper;
        this.createFinancialAccountUseCase = createFinancialAccountUseCase;
        this.deactivateFinancialAccountUseCase = deactivateFinancialAccountUseCase;
    }

    /** BR-044/BR-047/BR-048: name is required, opening balance cannot be negative, currency defaults to BRL. */
    @PostMapping
    public ResponseEntity<FinancialAccountDto> create(@PathVariable UUID companyId,
            @Valid @RequestBody CreateFinancialAccountRequest request) {
        var dto = createFinancialAccountUseCase.execute(companyId, request.name(),
                Money.brl(request.openingBalance()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public List<FinancialAccountDto> listByCompany(@PathVariable UUID companyId) {
        return financialAccountRepository.findAllByCompanyId(companyId).stream()
                .map(financialAccountMapper::toDto)
                .toList();
    }

    @GetMapping("/{accountId}")
    public FinancialAccountDto getById(@PathVariable UUID companyId, @PathVariable UUID accountId) {
        return financialAccountRepository.findById(accountId)
                .map(financialAccountMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, accountId));
    }

    /** BR-051/BR-052: the account must exist; deactivating removes it from use in new transactions. */
    @PatchMapping("/{accountId}/deactivate")
    public FinancialAccountDto deactivate(@PathVariable UUID companyId, @PathVariable UUID accountId) {
        return deactivateFinancialAccountUseCase.execute(accountId);
    }
}
