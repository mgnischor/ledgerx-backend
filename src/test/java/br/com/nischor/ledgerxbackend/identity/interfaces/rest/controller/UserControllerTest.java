package br.com.nischor.ledgerxbackend.identity.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.usecase.DeactivateUserUseCase;
import br.com.nischor.ledgerxbackend.identity.application.usecase.GrantRoleUseCase;
import br.com.nischor.ledgerxbackend.identity.application.usecase.RegisterUserUseCase;
import br.com.nischor.ledgerxbackend.identity.domain.exception.EmailAlreadyRegisteredException;
import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.util.Set;
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

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private GrantRoleUseCase grantRoleUseCase;

    @MockitoBean
    private DeactivateUserUseCase deactivateUserUseCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void registrationIsPubliclyAccessible() throws Exception {
        var dto = new UserDto(userId, "Jane Doe", "jane@example.com", Set.of(Role.COLLABORATOR), true);
        when(registerUserUseCase.execute(any(), any(), any())).thenReturn(dto);

        var body = """
                {"fullName":"Jane Doe","email":"jane@example.com","password":"Str0ng!Pass"}
                """;

        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void returns422WhenEmailAlreadyRegistered() throws Exception {
        when(registerUserUseCase.execute(any(), any(), any()))
                .thenThrow(new EmailAlreadyRegisteredException("jane@example.com"));

        var body = """
                {"fullName":"Jane Doe","email":"jane@example.com","password":"Str0ng!Pass"}
                """;

        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is(422));
    }

    @Test
    void rejectsWeakPassword() throws Exception {
        var body = """
                {"fullName":"Jane Doe","email":"jane@example.com","password":"weakpassword"}
                """;

        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsPasswordEqualToEmail() throws Exception {
        var body = """
                {"fullName":"Jane Doe","email":"Jane@Example.com","password":"Jane@Example.com"}
                """;

        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void grantsRoleForAnyAuthenticatedUser() throws Exception {
        // UserController has no @PreAuthorize on this endpoint; any authenticated caller can grant roles today.
        var dto = new UserDto(userId, "Jane Doe", "jane@example.com", Set.of(Role.MANAGER), true);
        when(grantRoleUseCase.execute(userId, Role.MANAGER)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/users/{userId}/roles", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"MANAGER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("MANAGER"));
    }

    @Test
    void grantRoleRequiresAuthentication() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{userId}/roles", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"MANAGER\"}"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void returns404WhenGrantingRoleToUnknownUser() throws Exception {
        when(grantRoleUseCase.execute(userId, Role.MANAGER))
                .thenThrow(new EntityNotFoundException(br.com.nischor.ledgerxbackend.identity.domain.model.User.class,
                        userId));

        mockMvc.perform(patch("/api/v1/users/{userId}/roles", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"MANAGER\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deactivatesUser() throws Exception {
        var dto = new UserDto(userId, "Jane Doe", "jane@example.com", Set.of(Role.COLLABORATOR), false);
        when(deactivateUserUseCase.execute(userId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/users/{userId}/deactivate", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
