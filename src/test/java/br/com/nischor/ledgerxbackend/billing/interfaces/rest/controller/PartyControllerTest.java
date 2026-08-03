package br.com.nischor.ledgerxbackend.billing.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.billing.application.dto.PartyDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.PartyMapper;
import br.com.nischor.ledgerxbackend.billing.application.usecase.CreatePartyUseCase;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
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

@WebMvcTest(controllers = PartyController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class PartyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PartyRepository partyRepository;

    @MockitoBean
    private PartyMapper partyMapper;

    @MockitoBean
    private CreatePartyUseCase createPartyUseCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID partyId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void createsPartyWithValidCpf() throws Exception {
        var dto = new PartyDto(partyId, companyId, "Jane Doe", "11144477735", "jane@example.com",
                PartyType.CUSTOMER);
        when(createPartyUseCase.execute(any(), any(), any(), any(), any())).thenReturn(dto);

        var body = """
                {"name":"Jane Doe","documentType":"CPF","document":"111.444.777-35",
                "email":"jane@example.com","type":"CUSTOMER"}
                """;

        mockMvc.perform(post("/api/v1/companies/{companyId}/parties", companyId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.document").value("11144477735"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void createsPartyWithValidCnpj() throws Exception {
        var dto = new PartyDto(partyId, companyId, "Acme Ltda", "11222333000181", "acme@example.com",
                PartyType.SUPPLIER);
        when(createPartyUseCase.execute(any(), any(), any(), any(), any())).thenReturn(dto);

        var body = """
                {"name":"Acme Ltda","documentType":"CNPJ","document":"11.222.333/0001-81",
                "email":"acme@example.com","type":"SUPPLIER"}
                """;

        mockMvc.perform(post("/api/v1/companies/{companyId}/parties", companyId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsInvalidCpfCheckDigits() throws Exception {
        var body = """
                {"name":"Jane Doe","documentType":"CPF","document":"111.111.111-11",
                "email":"jane@example.com","type":"CUSTOMER"}
                """;

        mockMvc.perform(post("/api/v1/companies/{companyId}/parties", companyId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsInvalidEmail() throws Exception {
        var body = """
                {"name":"Jane Doe","documentType":"CPF","document":"111.444.777-35",
                "email":"not-an-email","type":"CUSTOMER"}
                """;

        mockMvc.perform(post("/api/v1/companies/{companyId}/parties", companyId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void listsPartiesOfACompany() throws Exception {
        when(partyRepository.findAllByCompanyId(companyId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/companies/{companyId}/parties", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
