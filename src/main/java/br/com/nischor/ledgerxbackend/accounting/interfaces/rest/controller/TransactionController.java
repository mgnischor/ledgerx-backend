package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.RecordTransactionUseCase;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.CreateTransactionRequest;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final RecordTransactionUseCase recordTransactionUseCase;

    public TransactionController(RecordTransactionUseCase recordTransactionUseCase) {
        this.recordTransactionUseCase = recordTransactionUseCase;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> record(@Valid @RequestBody CreateTransactionRequest request) {
        var dto = recordTransactionUseCase.execute(request.financialAccountId(), request.categoryId(),
                request.type(), Money.brl(request.amount()), request.description(), request.occurredOn());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
