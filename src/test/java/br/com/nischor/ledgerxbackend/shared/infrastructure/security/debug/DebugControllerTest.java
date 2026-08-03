package br.com.nischor.ledgerxbackend.shared.infrastructure.security.debug;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DebugController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class DebugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(authorities = "PERMISSION_DEBUG")
    void returnsDiagnosticsForDeveloperRole() throws Exception {
        mockMvc.perform(get("/api/v1/debug/info")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsWithoutDebugAuthority() throws Exception {
        mockMvc.perform(get("/api/v1/debug/info")).andExpect(status().isForbidden());
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/debug/info")).andExpect(status().is3xxRedirection());
    }
}
