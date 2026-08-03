package br.com.nischor.ledgerxbackend.reporting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.reporting.application.query.CashFlowReportService;
import br.com.nischor.ledgerxbackend.reporting.application.query.CashFlowSummary;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ReportController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CashFlowReportService cashFlowReportService;

    private final UUID companyId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void returnsCashFlowSummaryForAValidRange() throws Exception {
        var from = LocalDate.of(2026, 1, 1);
        var to = LocalDate.of(2026, 1, 31);
        var summary = new CashFlowSummary(companyId, from, to, new BigDecimal("500.00"), new BigDecimal("200.00"),
                new BigDecimal("300.00"));
        when(cashFlowReportService.summarize(eq(companyId), eq(from), eq(to))).thenReturn(summary);

        mockMvc.perform(get("/api/v1/companies/{companyId}/reports/cash-flow", companyId)
                        .param("from", from.toString()).param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netResult").value(300.00));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsFromAfterTo() throws Exception {
        var from = LocalDate.of(2026, 2, 1);
        var to = LocalDate.of(2026, 1, 1);

        mockMvc.perform(get("/api/v1/companies/{companyId}/reports/cash-flow", companyId)
                        .param("from", from.toString()).param("to", to.toString()))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.error").value("Business Rule Violation"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsRangeLargerThan366Days() throws Exception {
        var from = LocalDate.of(2026, 1, 1);
        var to = from.plusDays(400);

        mockMvc.perform(get("/api/v1/companies/{companyId}/reports/cash-flow", companyId)
                        .param("from", from.toString()).param("to", to.toString()))
                .andExpect(status().is(422));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsMissingDateParams() throws Exception {
        mockMvc.perform(get("/api/v1/companies/{companyId}/reports/cash-flow", companyId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/companies/{companyId}/reports/cash-flow", companyId)
                        .param("from", "2026-01-01").param("to", "2026-01-31"))
                .andExpect(status().is3xxRedirection());
    }
}
