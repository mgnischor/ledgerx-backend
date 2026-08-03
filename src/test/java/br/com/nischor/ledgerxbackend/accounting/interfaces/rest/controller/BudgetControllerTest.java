package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetDto;
import br.com.nischor.ledgerxbackend.accounting.application.dto.BudgetStatusDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.BudgetMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateBudgetUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.DeactivateBudgetUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.GetBudgetStatusUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Budget;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.BudgetRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.math.BigDecimal;
import java.time.YearMonth;
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

@WebMvcTest(controllers = BudgetController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private BudgetRepository budgetRepository;

    @MockitoBean
    private BudgetMapper budgetMapper;

    @MockitoBean
    private CreateBudgetUseCase createBudgetUseCase;

    @MockitoBean
    private GetBudgetStatusUseCase getBudgetStatusUseCase;

    @MockitoBean
    private DeactivateBudgetUseCase deactivateBudgetUseCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID budgetId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void createsBudgetWhenAuthorized() throws Exception {
        var request = """
                {"categoryId":"%s","period":"%s","limit":500.00}
                """.formatted(categoryId, YearMonth.now());
        var dto = new BudgetDto(budgetId, companyId, categoryId, YearMonth.now(), new BigDecimal("500.00"), "BRL",
                true);
        when(createBudgetUseCase.execute(eq(companyId), eq(categoryId), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/companies/{companyId}/budgets", companyId).contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(budgetId.toString()));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsCreateWithoutCreateAuthority() throws Exception {
        var request = """
                {"categoryId":"%s","period":"%s","limit":500.00}
                """.formatted(categoryId, YearMonth.now());

        mockMvc.perform(post("/api/v1/companies/{companyId}/budgets", companyId).contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsUnauthenticatedRequests() throws Exception {
        // Form login is enabled alongside JWT auth, so an unauthenticated request is redirected to
        // the login page rather than receiving a 401.
        mockMvc.perform(get("/api/v1/companies/{companyId}/budgets", companyId))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsCreateWithNonPositiveLimit() throws Exception {
        var request = """
                {"categoryId":"%s","period":"%s","limit":-1}
                """.formatted(categoryId, YearMonth.now());

        mockMvc.perform(post("/api/v1/companies/{companyId}/budgets", companyId).contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void listsBudgetsOfACompany() throws Exception {
        when(budgetRepository.findAllByCompanyId(companyId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/companies/{companyId}/budgets", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void returnsBudgetStatus() throws Exception {
        var statusDto = new BudgetStatusDto(budgetId, categoryId, YearMonth.now(), new BigDecimal("500.00"),
                new BigDecimal("100.00"), new BigDecimal("400.00"), false);
        when(getBudgetStatusUseCase.execute(budgetId)).thenReturn(statusDto);

        mockMvc.perform(get("/api/v1/companies/{companyId}/budgets/{budgetId}/status", companyId, budgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overBudget").value(false));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void returns404WhenBudgetStatusNotFound() throws Exception {
        when(getBudgetStatusUseCase.execute(budgetId))
                .thenThrow(new EntityNotFoundException(Budget.class, budgetId));

        mockMvc.perform(get("/api/v1/companies/{companyId}/budgets/{budgetId}/status", companyId, budgetId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_DELETE")
    void deactivatesBudget() throws Exception {
        var dto = new BudgetDto(budgetId, companyId, categoryId, YearMonth.now(), new BigDecimal("500.00"), "BRL",
                false);
        when(deactivateBudgetUseCase.execute(budgetId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/companies/{companyId}/budgets/{budgetId}/deactivate", companyId, budgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
