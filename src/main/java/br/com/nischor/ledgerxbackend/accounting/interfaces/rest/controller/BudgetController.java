package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetStatusDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateBudgetUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.DeactivateBudgetUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.GetBudgetStatusUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.CreateBudgetRequest;
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

/**
 * REST controller exposing endpoints to create, list, inspect, and deactivate monthly budgets for a company's
 * expense categories.
 */
@RestController
@RequestMapping("/api/v1/companies/{companyId}/budgets")
@Tag(name = "Budgets", description = "Monthly spending limits set per expense category")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final CreateBudgetUseCase createBudgetUseCase;
    private final GetBudgetStatusUseCase getBudgetStatusUseCase;
    private final DeactivateBudgetUseCase deactivateBudgetUseCase;

    /**
     * Creates the controller.
     *
     * @param budgetRepository repository used to list budgets
     * @param budgetMapper mapper used to convert budgets into DTOs
     * @param createBudgetUseCase use case used to create a budget
     * @param getBudgetStatusUseCase use case used to compute a budget's spending status
     * @param deactivateBudgetUseCase use case used to deactivate a budget
     */
    public BudgetController(BudgetRepository budgetRepository, BudgetMapper budgetMapper,
            CreateBudgetUseCase createBudgetUseCase, GetBudgetStatusUseCase getBudgetStatusUseCase,
            DeactivateBudgetUseCase deactivateBudgetUseCase) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
        this.createBudgetUseCase = createBudgetUseCase;
        this.getBudgetStatusUseCase = getBudgetStatusUseCase;
        this.deactivateBudgetUseCase = deactivateBudgetUseCase;
    }

    /**
     * BR-101: period is required and cannot be in the past. BR-102: limit must be strictly
     * positive. BR-103: the referenced category must exist (404 otherwise). BR-104: budgets can
     * only be set for EXPENSE categories. BR-105: only one budget may exist per company, category
     * and period.
     *
     * @param companyId the identifier of the company the budget belongs to
     * @param request the budget creation payload
     * @return the created budget wrapped in a 201 response
     */
    @Operation(summary = "Create a monthly budget for an expense category", description = "BR-101..BR-105.")
    @ApiResponse(responseCode = "201", description = "Budget created")
    @ApiResponse(responseCode = "400", description = "Validation failure (past period, non-positive limit, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Category not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422",
            description = "Business rule violation (non-EXPENSE category, duplicate period)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<BudgetDto> create(@PathVariable UUID companyId,
            @Valid @RequestBody CreateBudgetRequest request) {
        var dto = createBudgetUseCase.execute(companyId, request.categoryId(), request.period(),
                Money.brl(request.limit()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Lists all budgets belonging to a company.
     *
     * @param companyId the identifier of the company
     * @return the list of budgets as DTOs
     */
    @Operation(summary = "List budgets of a company")
    @ApiResponse(responseCode = "200", description = "Budgets listed")
    @PreAuthorize(Authorizations.READ)
    @GetMapping
    public List<BudgetDto> listByCompany(@PathVariable UUID companyId) {
        return budgetRepository.findAllByCompanyId(companyId).stream().map(budgetMapper::toDto).toList();
    }

    /**
     * BR-106: status is computed from EXPENSE transactions recorded in the budget's category and period.
     *
     * @param companyId the identifier of the company the budget belongs to
     * @param budgetId the identifier of the budget to evaluate
     * @return the budget's spending status
     */
    @Operation(summary = "Get how much of a budget has been spent so far", description = "BR-106.")
    @ApiResponse(responseCode = "200", description = "Budget status computed")
    @ApiResponse(responseCode = "404", description = "Budget not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.READ)
    @GetMapping("/{budgetId}/status")
    public BudgetStatusDto getStatus(@PathVariable UUID companyId, @PathVariable UUID budgetId) {
        return getBudgetStatusUseCase.execute(budgetId);
    }

    /**
     * Deactivates a budget.
     *
     * @param companyId the identifier of the company the budget belongs to
     * @param budgetId the identifier of the budget to deactivate
     * @return the deactivated budget as a DTO
     */
    @Operation(summary = "Deactivate a budget")
    @ApiResponse(responseCode = "200", description = "Budget deactivated")
    @ApiResponse(responseCode = "404", description = "Budget not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.DELETE)
    @PatchMapping("/{budgetId}/deactivate")
    public BudgetDto deactivate(@PathVariable UUID companyId, @PathVariable UUID budgetId) {
        return deactivateBudgetUseCase.execute(budgetId);
    }
}
