package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.usecase.TransferFundsUseCase;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.TransferFundsRequest;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
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
    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferFundsRequest request) {
        transferFundsUseCase.execute(request.fromAccountId(), request.toAccountId(), Money.brl(request.amount()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
