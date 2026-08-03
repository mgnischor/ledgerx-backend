package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.accounting.application.dto.TransactionDto;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.RecordTransactionUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TransactionController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RecordTransactionUseCase recordTransactionUseCase;

    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID transactionId = UUID.randomUUID();

    private String requestBody(String type) {
        return """
                {"financialAccountId":"%s","categoryId":"%s","type":"%s","amount":50.00,
                "description":"Lunch","occurredOn":"%s"}
                """.formatted(accountId, categoryId, type, LocalDate.now());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void recordsTransactionWhenAuthorized() throws Exception {
        var dto = new TransactionDto(transactionId, accountId, categoryId, TransactionType.EXPENSE,
                new BigDecimal("50.00"), "Lunch", LocalDate.now());
        when(recordTransactionUseCase.execute(any(), any(), any(), any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("EXPENSE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId.toString()));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsTransferType() throws Exception {
        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("TRANSFER")))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.error").value("Business Rule Violation"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsWithoutCreateAuthority() throws Exception {
        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("EXPENSE")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsNonPositiveAmount() throws Exception {
        var body = """
                {"financialAccountId":"%s","categoryId":"%s","type":"EXPENSE","amount":0,
                "description":"Lunch","occurredOn":"%s"}
                """.formatted(accountId, categoryId, LocalDate.now());

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsFutureOccurredOnDate() throws Exception {
        var body = """
                {"financialAccountId":"%s","categoryId":"%s","type":"EXPENSE","amount":50.00,
                "description":"Lunch","occurredOn":"%s"}
                """.formatted(accountId, categoryId, LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
