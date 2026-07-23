package br.com.nischor.ledgerxbackend.billing.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.application.usecase.CancelInvoiceUseCase;
import br.com.nischor.ledgerxbackend.billing.application.usecase.IssueInvoiceUseCase;
import br.com.nischor.ledgerxbackend.billing.application.usecase.RegisterPaymentUseCase;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto.CreateInvoiceRequest;
import br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto.RegisterPaymentRequest;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final IssueInvoiceUseCase issueInvoiceUseCase;
    private final RegisterPaymentUseCase registerPaymentUseCase;
    private final CancelInvoiceUseCase cancelInvoiceUseCase;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceController(IssueInvoiceUseCase issueInvoiceUseCase, RegisterPaymentUseCase registerPaymentUseCase,
            CancelInvoiceUseCase cancelInvoiceUseCase, InvoiceRepository invoiceRepository,
            InvoiceMapper invoiceMapper) {
        this.issueInvoiceUseCase = issueInvoiceUseCase;
        this.registerPaymentUseCase = registerPaymentUseCase;
        this.cancelInvoiceUseCase = cancelInvoiceUseCase;
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    /**
     * BR-081..BR-090: companyId/partyId/direction are required, the party must exist,
     * installment amounts must be non-empty, positive and capped at 60, firstDueDate cannot be
     * in the past, and installments are due monthly starting on firstDueDate.
     */
    @PostMapping
    public ResponseEntity<InvoiceDto> issue(@Valid @RequestBody CreateInvoiceRequest request) {
        var amounts = request.installmentAmounts().stream().map(Money::brl).toList();
        var dto = issueInvoiceUseCase.execute(request.companyId(), request.partyId(), request.direction(), amounts,
                request.firstDueDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceDto getById(@PathVariable UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(invoiceMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(Invoice.class, invoiceId));
    }

    /**
     * BR-091..BR-098: the invoice and installment must exist, a canceled invoice cannot receive
     * payments, paidOn cannot be in the future, and the invoice status transitions to
     * PARTIALLY_PAID/PAID as installments are settled, publishing an event once fully paid.
     */
    @PatchMapping("/{invoiceId}/payments")
    public InvoiceDto registerPayment(@PathVariable UUID invoiceId, @Valid @RequestBody RegisterPaymentRequest request) {
        return registerPaymentUseCase.execute(invoiceId, request.installmentId(), request.paidOn());
    }

    /** BR-099/BR-100/BR-101: the invoice must exist, a fully paid invoice cannot be canceled, and canceling twice is a no-op. */
    @PatchMapping("/{invoiceId}/cancel")
    public InvoiceDto cancel(@PathVariable UUID invoiceId) {
        return cancelInvoiceUseCase.execute(invoiceId);
    }
}
