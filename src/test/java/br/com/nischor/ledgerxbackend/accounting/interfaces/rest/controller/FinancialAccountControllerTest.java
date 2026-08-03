package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.accounting.application.dto.FinancialAccountDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.FinancialAccountMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateFinancialAccountUseCase;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.DeactivateFinancialAccountUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

@WebMvcTest(controllers = FinancialAccountController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class FinancialAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private FinancialAccountRepository financialAccountRepository;

    @MockitoBean
    private FinancialAccountMapper financialAccountMapper;

    @MockitoBean
    private CreateFinancialAccountUseCase createFinancialAccountUseCase;

    @MockitoBean
    private DeactivateFinancialAccountUseCase deactivateFinancialAccountUseCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void createsFinancialAccountWhenAuthorized() throws Exception {
        var dto = new FinancialAccountDto(accountId, companyId, "Checking", new BigDecimal("100.00"), "BRL", true);
        when(createFinancialAccountUseCase.execute(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/companies/{companyId}/financial-accounts", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyId\":\"%s\",\"name\":\"Checking\",\"openingBalance\":100.00}"
                                .formatted(companyId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Checking"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsNegativeOpeningBalance() throws Exception {
        mockMvc.perform(post("/api/v1/companies/{companyId}/financial-accounts", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyId\":\"%s\",\"name\":\"Checking\",\"openingBalance\":-1}"
                                .formatted(companyId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void listsFinancialAccountsOfACompany() throws Exception {
        when(financialAccountRepository.findAllByCompanyId(companyId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/companies/{companyId}/financial-accounts", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void getsFinancialAccountById() throws Exception {
        var account = mockAccount();
        var dto = new FinancialAccountDto(accountId, companyId, "Checking", new BigDecimal("100.00"), "BRL", true);
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(financialAccountMapper.toDto(account)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/companies/{companyId}/financial-accounts/{accountId}", companyId, accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void returns404WhenFinancialAccountNotFound() throws Exception {
        when(financialAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/companies/{companyId}/financial-accounts/{accountId}", companyId, accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_DELETE")
    void deactivatesFinancialAccount() throws Exception {
        var dto = new FinancialAccountDto(accountId, companyId, "Checking", new BigDecimal("100.00"), "BRL", false);
        when(deactivateFinancialAccountUseCase.execute(accountId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/companies/{companyId}/financial-accounts/{accountId}/deactivate", companyId,
                        accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    private FinancialAccount mockAccount() {
        return new FinancialAccount(accountId, companyId, "Checking", Money.brl(new BigDecimal("100.00")));
    }
}
