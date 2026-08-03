package br.com.nischor.ledgerxbackend.billing.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.billing.application.dto.InvoiceDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.InvoiceMapper;
import br.com.nischor.ledgerxbackend.billing.application.usecase.CancelInvoiceUseCase;
import br.com.nischor.ledgerxbackend.billing.application.usecase.IssueInvoiceUseCase;
import br.com.nischor.ledgerxbackend.billing.application.usecase.RegisterPaymentUseCase;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import java.util.Optional;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = InvoiceController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private IssueInvoiceUseCase issueInvoiceUseCase;

    @MockitoBean
    private RegisterPaymentUseCase registerPaymentUseCase;

    @MockitoBean
    private CancelInvoiceUseCase cancelInvoiceUseCase;

    @MockitoBean
    private InvoiceRepository invoiceRepository;

    @MockitoBean
    private InvoiceMapper invoiceMapper;

    private final UUID companyId = UUID.randomUUID();
    private final UUID partyId = UUID.randomUUID();
    private final UUID invoiceId = UUID.randomUUID();
    private final UUID installmentId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void issuesInvoiceWhenAuthorized() throws Exception {
        var dto = new InvoiceDto(invoiceId, companyId, partyId, PartyType.CUSTOMER, InvoiceStatus.OPEN, 2);
        when(issueInvoiceUseCase.execute(any(), any(), any(), any(), any())).thenReturn(dto);

        var body = """
                {"companyId":"%s","partyId":"%s","direction":"CUSTOMER","installmentAmounts":[100.00,100.00],
                "firstDueDate":"%s"}
                """.formatted(companyId, partyId, LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/v1/invoices").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(invoiceId.toString()));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsEmptyInstallments() throws Exception {
        var body = """
                {"companyId":"%s","partyId":"%s","direction":"CUSTOMER","installmentAmounts":[],
                "firstDueDate":"%s"}
                """.formatted(companyId, partyId, LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/v1/invoices").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void getsInvoiceById() throws Exception {
        var invoice = org.mockito.Mockito.mock(br.com.nischor.ledgerxbackend.billing.domain.model.Invoice.class);
        var dto = new InvoiceDto(invoiceId, companyId, partyId, PartyType.CUSTOMER, InvoiceStatus.OPEN, 2);
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toDto(invoice)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/invoices/{invoiceId}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void returns404WhenInvoiceNotFound() throws Exception {
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/invoices/{invoiceId}", invoiceId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_APPROVE")
    void registersPaymentWhenAuthorized() throws Exception {
        var dto = new InvoiceDto(invoiceId, companyId, partyId, PartyType.CUSTOMER, InvoiceStatus.PARTIALLY_PAID, 2);
        when(registerPaymentUseCase.execute(any(), any(), any())).thenReturn(dto);

        var body = """
                {"installmentId":"%s","paidOn":"%s"}
                """.formatted(installmentId, LocalDate.now());

        mockMvc.perform(patch("/api/v1/invoices/{invoiceId}/payments", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTIALLY_PAID"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsRegisterPaymentWithoutApproveAuthority() throws Exception {
        var body = """
                {"installmentId":"%s","paidOn":"%s"}
                """.formatted(installmentId, LocalDate.now());

        mockMvc.perform(patch("/api/v1/invoices/{invoiceId}/payments", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_DELETE")
    void cancelsInvoice() throws Exception {
        var dto = new InvoiceDto(invoiceId, companyId, partyId, PartyType.CUSTOMER, InvoiceStatus.CANCELED, 2);
        when(cancelInvoiceUseCase.execute(invoiceId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/invoices/{invoiceId}/cancel", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }
}
