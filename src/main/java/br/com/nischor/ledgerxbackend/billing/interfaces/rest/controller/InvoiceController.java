package br.com.nischor.ledgerxbackend.billing.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.usecase.IssueInvoiceUseCase;
import br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto.CreateInvoiceRequest;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final IssueInvoiceUseCase issueInvoiceUseCase;

    public InvoiceController(IssueInvoiceUseCase issueInvoiceUseCase) {
        this.issueInvoiceUseCase = issueInvoiceUseCase;
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> issue(@Valid @RequestBody CreateInvoiceRequest request) {
        var amounts = request.installmentAmounts().stream().map(Money::brl).toList();
        var dto = issueInvoiceUseCase.execute(request.companyId(), request.partyId(), request.direction(), amounts,
                request.firstDueDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
