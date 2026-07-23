package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateRecurringTransactionRuleUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.DeactivateRecurringTransactionRuleUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.GenerateDueRecurringTransactionsUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.CreateRecurringTransactionRuleRequest;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/recurring-transactions")
@Tag(name = "Recurring Transactions",
        description = "Templates for recurring income/expense (subscriptions, rent, payroll) that are "
                + "materialized into real transactions on demand")
public class RecurringTransactionRuleController {

    private final RecurringTransactionRuleRepository recurringTransactionRuleRepository;
    private final RecurringTransactionRuleMapper mapper;
    private final CreateRecurringTransactionRuleUseCase createRecurringTransactionRuleUseCase;
    private final DeactivateRecurringTransactionRuleUseCase deactivateRecurringTransactionRuleUseCase;
    private final GenerateDueRecurringTransactionsUseCase generateDueRecurringTransactionsUseCase;

    public RecurringTransactionRuleController(
            RecurringTransactionRuleRepository recurringTransactionRuleRepository,
            RecurringTransactionRuleMapper mapper,
            CreateRecurringTransactionRuleUseCase createRecurringTransactionRuleUseCase,
            DeactivateRecurringTransactionRuleUseCase deactivateRecurringTransactionRuleUseCase,
            GenerateDueRecurringTransactionsUseCase generateDueRecurringTransactionsUseCase) {
        this.recurringTransactionRuleRepository = recurringTransactionRuleRepository;
        this.mapper = mapper;
        this.createRecurringTransactionRuleUseCase = createRecurringTransactionRuleUseCase;
        this.deactivateRecurringTransactionRuleUseCase = deactivateRecurringTransactionRuleUseCase;
        this.generateDueRecurringTransactionsUseCase = generateDueRecurringTransactionsUseCase;
    }

    /**
     * BR-107: amount must be strictly positive. BR-108: description is capped at 255 characters.
     * BR-109: frequency is required (WEEKLY, MONTHLY or YEARLY). BR-110: firstOccurrence is
     * required and cannot be in the past. BR-111: the referenced category must exist and its type
     * must match the rule's type. BR-112: TRANSFER is rejected, matching the single-account
     * transaction endpoint.
     */
    @Operation(summary = "Create a recurring transaction rule", description = "BR-107..BR-112.")
    @ApiResponse(responseCode = "201", description = "Recurring transaction rule created")
    @ApiResponse(responseCode = "400", description = "Validation failure (non-positive amount, past date, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Category not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422",
            description = "Business rule violation (TRANSFER type used here, category/type mismatch)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<RecurringTransactionRuleDto> create(@PathVariable UUID companyId,
            @Valid @RequestBody CreateRecurringTransactionRuleRequest request) {
        if (request.type() == TransactionType.TRANSFER) {
            throw new BusinessRuleViolationException(
                    "TRANSFER is not supported for recurring transaction rules");
        }

        var dto = createRecurringTransactionRuleUseCase.execute(companyId, request.financialAccountId(),
                request.categoryId(), request.type(), Money.brl(request.amount()), request.description(),
                request.frequency(), request.firstOccurrence());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "List recurring transaction rules of a company")
    @ApiResponse(responseCode = "200", description = "Recurring transaction rules listed")
    @GetMapping
    public List<RecurringTransactionRuleDto> listByCompany(@PathVariable UUID companyId) {
        return recurringTransactionRuleRepository.findAllByCompanyId(companyId).stream().map(mapper::toDto).toList();
    }

    /**
     * BR-113: every active rule whose {@code nextOccurrence} is on or before today is
     * materialized into a real transaction (through {@code RecordTransactionUseCase}, so the same
     * balance rules apply) and advanced to its next occurrence.
     */
    @Operation(summary = "Generate transactions for every rule that is currently due",
            description = "BR-113. Safe to call repeatedly (e.g. from a daily scheduler); rules that "
                    + "are not yet due are skipped.")
    @ApiResponse(responseCode = "200", description = "Due transactions generated (may be an empty list)")
    @PostMapping("/generate-due")
    public List<TransactionDto> generateDue(@PathVariable UUID companyId) {
        return generateDueRecurringTransactionsUseCase.execute(companyId);
    }

    /** BR-114: deactivating a rule stops it from being included in future generate-due runs. */
    @Operation(summary = "Deactivate a recurring transaction rule", description = "BR-114.")
    @ApiResponse(responseCode = "200", description = "Recurring transaction rule deactivated")
    @ApiResponse(responseCode = "404", description = "Recurring transaction rule not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PatchMapping("/{ruleId}/deactivate")
    public RecurringTransactionRuleDto deactivate(@PathVariable UUID companyId, @PathVariable UUID ruleId) {
        return deactivateRecurringTransactionRuleUseCase.execute(ruleId);
    }
}
