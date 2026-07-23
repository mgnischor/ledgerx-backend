package br.com.nischor.ledgerxbackend.reporting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.reporting.application.query.CashFlowReportService;
import br.com.nischor.ledgerxbackend.reporting.application.query.CashFlowSummary;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/reports")
@Tag(name = "Reports", description = "Read-only cash-flow and financial reports")
public class ReportController {

    private static final long MAX_RANGE_DAYS = 366;

    private final CashFlowReportService cashFlowReportService;

    public ReportController(CashFlowReportService cashFlowReportService) {
        this.cashFlowReportService = cashFlowReportService;
    }

    /**
     * BR-103..BR-106: {@code from} and {@code to} are required (missing/malformed query params
     * are rejected by Spring's binding before this method runs, returning 400 Bad Request);
     * {@code from} must not be after {@code to}, and the window cannot exceed 366 days.
     */
    @Operation(summary = "Get a cash-flow summary for a period",
            description = "Only INCOME and EXPENSE transactions are considered; TRANSFER is excluded to avoid "
                    + "double counting. BR-102..BR-108.")
    @ApiResponse(responseCode = "200", description = "Cash-flow summary computed")
    @ApiResponse(responseCode = "422", description = "'from' after 'to', or window larger than 366 days",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/cash-flow")
    public CashFlowSummary cashFlow(@PathVariable UUID companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from.isAfter(to)) {
            throw new BusinessRuleViolationException("'from' must not be after 'to'");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new BusinessRuleViolationException(
                    "The reporting window cannot exceed %d days".formatted(MAX_RANGE_DAYS));
        }

        return cashFlowReportService.summarize(companyId, from, to);
    }
}
