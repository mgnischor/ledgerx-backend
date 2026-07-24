package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.usecase.TransferFundsUseCase;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.TransferFundsRequest;
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
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transfers", description = "Fund transfers between two financial accounts")
public class TransferController {

    private final TransferFundsUseCase transferFundsUseCase;

    public TransferController(TransferFundsUseCase transferFundsUseCase) {
        this.transferFundsUseCase = transferFundsUseCase;
    }

    /**
     * BR-070/BR-071: source and destination accounts must be different (enforced by
     * {@link TransferFundsRequest}'s class-level constraint) and the amount must be positive.
     * BR-072/BR-073: both legs are debited/credited atomically, and the transfer fails if the
     * source account has insufficient balance.
     */
    @Operation(summary = "Transfer funds between two financial accounts", description = "BR-070..BR-073.")
    @ApiResponse(responseCode = "204", description = "Transfer completed")
    @ApiResponse(responseCode = "400", description = "Validation failure (same account, non-positive amount)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Source or destination account not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Insufficient balance on the source account",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferFundsRequest request) {
        transferFundsUseCase.execute(request.fromAccountId(), request.toAccountId(), Money.brl(request.amount()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
