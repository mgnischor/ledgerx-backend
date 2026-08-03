package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.accounting.application.dto.RecurringTransactionRuleDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.RecurringTransactionRuleMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateRecurringTransactionRuleUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.DeactivateRecurringTransactionRuleUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.GenerateDueRecurringTransactionsUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.RecurrenceFrequency;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.RecurringTransactionRuleRepository;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

@WebMvcTest(controllers = RecurringTransactionRuleController.class,
        excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class RecurringTransactionRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RecurringTransactionRuleRepository recurringTransactionRuleRepository;

    @MockitoBean
    private RecurringTransactionRuleMapper recurringTransactionRuleMapper;

    @MockitoBean
    private CreateRecurringTransactionRuleUseCase createRecurringTransactionRuleUseCase;

    @MockitoBean
    private DeactivateRecurringTransactionRuleUseCase deactivateRecurringTransactionRuleUseCase;

    @MockitoBean
    private GenerateDueRecurringTransactionsUseCase generateDueRecurringTransactionsUseCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID ruleId = UUID.randomUUID();

    private String requestBody(String type) {
        return """
                {"financialAccountId":"%s","categoryId":"%s","type":"%s","amount":100.00,
                "description":"Rent","frequency":"MONTHLY","firstOccurrence":"%s"}
                """.formatted(accountId, categoryId, type, LocalDate.now());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void createsRuleWhenAuthorized() throws Exception {
        var dto = new RecurringTransactionRuleDto(ruleId, companyId, accountId, categoryId, TransactionType.EXPENSE,
                new BigDecimal("100.00"), "Rent", RecurrenceFrequency.MONTHLY, LocalDate.now(), true);
        when(createRecurringTransactionRuleUseCase.execute(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/companies/{companyId}/recurring-transactions", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("EXPENSE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ruleId.toString()));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsTransferType() throws Exception {
        mockMvc.perform(post("/api/v1/companies/{companyId}/recurring-transactions", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody("TRANSFER")))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.error").value("Business Rule Violation"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void listsRulesOfACompany() throws Exception {
        when(recurringTransactionRuleRepository.findAllByCompanyId(companyId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/companies/{companyId}/recurring-transactions", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void generatesDueTransactions() throws Exception {
        when(generateDueRecurringTransactionsUseCase.execute(companyId)).thenReturn(List.of());

        mockMvc.perform(post("/api/v1/companies/{companyId}/recurring-transactions/generate-due", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_DELETE")
    void deactivatesRule() throws Exception {
        var dto = new RecurringTransactionRuleDto(ruleId, companyId, accountId, categoryId, TransactionType.EXPENSE,
                new BigDecimal("100.00"), "Rent", RecurrenceFrequency.MONTHLY, LocalDate.now(), false);
        when(deactivateRecurringTransactionRuleUseCase.execute(ruleId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/companies/{companyId}/recurring-transactions/{ruleId}/deactivate", companyId,
                        ruleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
