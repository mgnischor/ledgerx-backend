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
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/financial-accounts")
@Tag(name = "Financial Accounts", description = "Cash/bank accounts held by a company")
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
    @Operation(summary = "Create a financial account", description = "BR-044/BR-047/BR-048.")
    @ApiResponse(responseCode = "201", description = "Financial account created")
    @ApiResponse(responseCode = "400", description = "Validation failure (blank name, negative balance, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<FinancialAccountDto> create(@PathVariable UUID companyId,
            @Valid @RequestBody CreateFinancialAccountRequest request) {
        var dto = createFinancialAccountUseCase.execute(companyId, request.name(),
                Money.brl(request.openingBalance()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "List financial accounts of a company")
    @ApiResponse(responseCode = "200", description = "Financial accounts listed")
    @PreAuthorize(Authorizations.READ)
    @GetMapping
    public List<FinancialAccountDto> listByCompany(@PathVariable UUID companyId) {
        return financialAccountRepository.findAllByCompanyId(companyId).stream()
                .map(financialAccountMapper::toDto)
                .toList();
    }

    @Operation(summary = "Get a financial account by id")
    @ApiResponse(responseCode = "200", description = "Financial account found")
    @ApiResponse(responseCode = "404", description = "Financial account not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.READ)
    @GetMapping("/{accountId}")
    public FinancialAccountDto getById(@PathVariable UUID companyId, @PathVariable UUID accountId) {
        return financialAccountRepository.findById(accountId)
                .map(financialAccountMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(FinancialAccount.class, accountId));
    }

    /** BR-051/BR-052: the account must exist; deactivating removes it from use in new transactions. */
    @Operation(summary = "Deactivate a financial account", description = "BR-051/BR-052.")
    @ApiResponse(responseCode = "200", description = "Financial account deactivated")
    @ApiResponse(responseCode = "404", description = "Financial account not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.DELETE)
    @PatchMapping("/{accountId}/deactivate")
    public FinancialAccountDto deactivate(@PathVariable UUID companyId, @PathVariable UUID accountId) {
        return deactivateFinancialAccountUseCase.execute(accountId);
    }
}
