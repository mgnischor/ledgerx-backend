package br.com.nischor.ledgerxbackend.reporting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.reporting.application.query.CashFlowReportService;
import br.com.nischor.ledgerxbackend.reporting.application.query.CashFlowSummary;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/reports")
public class ReportController {

    private final CashFlowReportService cashFlowReportService;

    public ReportController(CashFlowReportService cashFlowReportService) {
        this.cashFlowReportService = cashFlowReportService;
    }

    @GetMapping("/cash-flow")
    public CashFlowSummary cashFlow(@PathVariable UUID companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return cashFlowReportService.summarize(companyId, from, to);
    }
}
