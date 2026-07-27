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
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * REST controller exposing endpoints to issue invoices, register installment payments and
 * cancel invoices for accounts receivable/payable.
 */
@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices", description = "Accounts receivable/payable invoices and their installments")
public class InvoiceController {

    private final IssueInvoiceUseCase issueInvoiceUseCase;
    private final RegisterPaymentUseCase registerPaymentUseCase;
    private final CancelInvoiceUseCase cancelInvoiceUseCase;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    /**
     * Creates the controller.
     *
     * @param issueInvoiceUseCase use case used to issue new invoices
     * @param registerPaymentUseCase use case used to register installment payments
     * @param cancelInvoiceUseCase use case used to cancel invoices
     * @param invoiceRepository repository used to look up invoices for read-only endpoints
     * @param invoiceMapper mapper used to convert invoices to their DTO representation
     */
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
     * Handles {@code POST /api/v1/invoices} to issue a new invoice with its installments.
     *
     * <p>BR-067..BR-074: companyId/partyId/direction are required, the party must exist,
     * installment amounts must be non-empty, positive and capped at 60, firstDueDate cannot be
     * in the past, and installments are due monthly starting on firstDueDate.
     *
     * @param request the validated request body describing the invoice to issue
     * @return a {@code 201 Created} response with the issued invoice
     */
    @Operation(summary = "Issue an invoice (accounts receivable or payable)", description = "BR-067..BR-074.")
    @ApiResponse(responseCode = "201", description = "Invoice issued")
    @ApiResponse(responseCode = "400", description = "Validation failure (empty installments, past due date, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Party not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Non-positive installment amount",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<InvoiceDto> issue(@Valid @RequestBody CreateInvoiceRequest request) {
        var amounts = request.installmentAmounts().stream().map(Money::brl).toList();
        var dto = issueInvoiceUseCase.execute(request.companyId(), request.partyId(), request.direction(), amounts,
                request.firstDueDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Handles {@code GET /api/v1/invoices/{invoiceId}} to retrieve an invoice by its identifier.
     *
     * @param invoiceId the identifier of the invoice to retrieve
     * @return the matching invoice as a DTO
     * @throws EntityNotFoundException if no invoice with the given identifier exists
     */
    @Operation(summary = "Get an invoice by id")
    @ApiResponse(responseCode = "200", description = "Invoice found")
    @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.READ)
    @GetMapping("/{invoiceId}")
    public InvoiceDto getById(@PathVariable UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(invoiceMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(Invoice.class, invoiceId));
    }

    /**
     * Handles {@code PATCH /api/v1/invoices/{invoiceId}/payments} to register a payment for one
     * of the invoice's installments.
     *
     * <p>BR-075..BR-081: the invoice and installment must exist, a canceled invoice cannot receive
     * payments, paidOn cannot be in the future, and the invoice status transitions to
     * PARTIALLY_PAID/PAID as installments are settled, publishing an event once fully paid.
     *
     * @param invoiceId the identifier of the invoice to register the payment against
     * @param request the validated request body identifying the installment and payment date
     * @return the updated invoice as a DTO
     */
    @Operation(summary = "Register a payment for an installment", description = "BR-075..BR-081.")
    @ApiResponse(responseCode = "200", description = "Payment registered")
    @ApiResponse(responseCode = "400", description = "Validation failure (future paidOn date)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422",
            description = "Business rule violation (canceled invoice, installment does not belong to invoice)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.APPROVE)
    @PatchMapping("/{invoiceId}/payments")
    public InvoiceDto registerPayment(@PathVariable UUID invoiceId, @Valid @RequestBody RegisterPaymentRequest request) {
        return registerPaymentUseCase.execute(invoiceId, request.installmentId(), request.paidOn());
    }

    /**
     * Handles {@code PATCH /api/v1/invoices/{invoiceId}/cancel} to cancel an invoice.
     *
     * <p>BR-082/BR-083/BR-084: the invoice must exist, a fully paid invoice cannot be canceled,
     * and canceling twice is a no-op.
     *
     * @param invoiceId the identifier of the invoice to cancel
     * @return the canceled invoice as a DTO
     */
    @Operation(summary = "Cancel an invoice", description = "Idempotent unless the invoice is fully paid. BR-082..BR-084.")
    @ApiResponse(responseCode = "200", description = "Invoice canceled")
    @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Invoice is fully paid and cannot be canceled",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.DELETE)
    @PatchMapping("/{invoiceId}/cancel")
    public InvoiceDto cancel(@PathVariable UUID invoiceId) {
        return cancelInvoiceUseCase.execute(invoiceId);
    }
}
