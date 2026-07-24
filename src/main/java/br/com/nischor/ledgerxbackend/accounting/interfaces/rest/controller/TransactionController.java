package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.RecordTransactionUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.CreateTransactionRequest;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Income and expense transactions recorded against a financial account")
public class TransactionController {

    private final RecordTransactionUseCase recordTransactionUseCase;

    public TransactionController(RecordTransactionUseCase recordTransactionUseCase) {
        this.recordTransactionUseCase = recordTransactionUseCase;
    }

    /**
     * BR-057..BR-067: required fields, positive amount, non-future/non-stale date and
     * description length are enforced by {@link CreateTransactionRequest}'s bean validation
     * constraints. BR-068: TRANSFER is rejected here explicitly since transfers must move
     * through {@code POST /api/v1/transfers} instead, so both legs are updated atomically.
     */
    @Operation(summary = "Record an income or expense transaction",
            description = "TRANSFER type is rejected here; use POST /api/v1/transfers instead. BR-057..BR-069.")
    @ApiResponse(responseCode = "201", description = "Transaction recorded")
    @ApiResponse(responseCode = "400", description = "Validation failure (non-positive amount, future date, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Financial account or category not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422",
            description = "Business rule violation (TRANSFER type used here, category/type mismatch, "
                    + "insufficient balance)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<TransactionDto> record(@Valid @RequestBody CreateTransactionRequest request) {
        if (request.type() == TransactionType.TRANSFER) {
            throw new BusinessRuleViolationException(
                    "TRANSFER transactions must be recorded through POST /api/v1/transfers, not this endpoint");
        }

        var dto = recordTransactionUseCase.execute(request.financialAccountId(), request.categoryId(),
                request.type(), Money.brl(request.amount()), request.description(), request.occurredOn());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
