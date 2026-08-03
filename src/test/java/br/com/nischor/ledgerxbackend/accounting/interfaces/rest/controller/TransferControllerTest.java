package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.accounting.application.usecase.TransferFundsUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.exception.InsufficientBalanceException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
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

@WebMvcTest(controllers = TransferController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TransferFundsUseCase transferFundsUseCase;

    private final UUID fromAccountId = UUID.randomUUID();
    private final UUID toAccountId = UUID.randomUUID();

    private String requestBody(UUID from, UUID to) {
        return """
                {"fromAccountId":"%s","toAccountId":"%s","amount":25.00}
                """.formatted(from, to);
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void transfersFundsWhenAuthorized() throws Exception {
        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(fromAccountId, toAccountId)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsSameSourceAndDestinationAccount() throws Exception {
        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(fromAccountId, fromAccountId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void returns404WhenAccountNotFound() throws Exception {
        doThrow(new EntityNotFoundException(br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount.class,
                fromAccountId)).when(transferFundsUseCase).execute(any(), any(), any());

        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(fromAccountId, toAccountId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void returns422WhenBalanceIsInsufficient() throws Exception {
        doThrow(new InsufficientBalanceException("Checking")).when(transferFundsUseCase)
                .execute(any(), any(), any());

        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(fromAccountId, toAccountId)))
                .andExpect(status().is(422));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsWithoutCreateAuthority() throws Exception {
        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(fromAccountId, toAccountId)))
                .andExpect(status().isForbidden());
    }
}
