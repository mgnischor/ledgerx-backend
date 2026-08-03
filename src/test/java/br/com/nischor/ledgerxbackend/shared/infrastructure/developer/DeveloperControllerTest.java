package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DeveloperController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DeveloperInfoService developerInfoService;

    @Test
    @WithMockUser(authorities = "PERMISSION_DEBUG")
    void returnsDiagnosticsForDeveloperRole() throws Exception {
        var now = Instant.now();
        var dto = new DeveloperInfoDto(new ApplicationInfoDto("ledgerx-backend", List.of("test"), now),
                new OperatingSystemInfoDto("Linux", "1.0", "amd64", "localhost"),
                new CpuInfoDto("amd64", 4, 0.5, 0.1, 0.1),
                new MemoryInfoDto(1, 2, 1, 4, 2, 2),
                new StorageInfoDto("/", 100, 50, 50),
                new ServiceVersionsDto("4.0", "16", "10"),
                new JavaRuntimeInfoDto("Eclipse Adoptium", "26", "OpenJDK 64-Bit Server VM", "26", "/opt/java", 1,
                        now, 1000));
        when(developerInfoService.collect()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/developer")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsWithoutDebugAuthority() throws Exception {
        mockMvc.perform(get("/api/v1/developer")).andExpect(status().isForbidden());
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/developer")).andExpect(status().is3xxRedirection());
    }
}
