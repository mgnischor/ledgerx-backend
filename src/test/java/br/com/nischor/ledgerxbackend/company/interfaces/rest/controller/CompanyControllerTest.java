package br.com.nischor.ledgerxbackend.company.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.usecase.DeactivateCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.application.usecase.RegisterCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
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

@WebMvcTest(controllers = CompanyController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RegisterCompanyUseCase registerCompanyUseCase;

    @MockitoBean
    private DeactivateCompanyUseCase deactivateCompanyUseCase;

    private final UUID companyId = UUID.randomUUID();

    private String validRequestBody() {
        return """
                {"legalName":"Acme Ltda","tradeName":"Acme","cnpj":"11.222.333/0001-81","size":"MICRO",
                "street":"Main St","number":"100","city":"Sao Paulo","state":"SP","zipCode":"01310-100",
                "country":"Brazil"}
                """;
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void registersCompanyWhenAuthorized() throws Exception {
        var dto = new CompanyDto(companyId, "Acme Ltda", "Acme", "11222333000181", CompanySize.MICRO, true);
        when(registerCompanyUseCase.execute(any(), any(), any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(companyId.toString()));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsWithoutCreateAuthority() throws Exception {
        mockMvc.perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsInvalidCnpj() throws Exception {
        var body = """
                {"legalName":"Acme Ltda","tradeName":"Acme","cnpj":"11.111.111/1111-11","size":"MICRO",
                "street":"Main St","number":"100","city":"Sao Paulo","state":"SP","zipCode":"01310-100",
                "country":"Brazil"}
                """;

        mockMvc.perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsInvalidState() throws Exception {
        var body = """
                {"legalName":"Acme Ltda","tradeName":"Acme","cnpj":"11.222.333/0001-81","size":"MICRO",
                "street":"Main St","number":"100","city":"Sao Paulo","state":"ZZ","zipCode":"01310-100",
                "country":"Brazil"}
                """;

        mockMvc.perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsInvalidZipCode() throws Exception {
        var body = """
                {"legalName":"Acme Ltda","tradeName":"Acme","cnpj":"11.222.333/0001-81","size":"MICRO",
                "street":"Main St","number":"100","city":"Sao Paulo","state":"SP","zipCode":"invalid",
                "country":"Brazil"}
                """;

        mockMvc.perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_DELETE")
    void deactivatesCompany() throws Exception {
        var dto = new CompanyDto(companyId, "Acme Ltda", "Acme", "11222333000181", CompanySize.MICRO, false);
        when(deactivateCompanyUseCase.execute(companyId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/companies/{companyId}/deactivate", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
