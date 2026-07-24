package br.com.nischor.ledgerxbackend.shared.infrastructure.security.debug;

import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Arrays;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Diagnostics available only to the {@code DEVELOPER} role's debug-mode tooling; see
 * {@link DebugModeFilter} for the accompanying per-request tracing headers.
 */
@RestController
@RequestMapping("/api/v1/debug")
@Tag(name = "Debug", description = "Developer-only diagnostics and debug-mode tooling")
public class DebugController {

    private final Environment environment;

    public DebugController(Environment environment) {
        this.environment = environment;
    }

    @Operation(summary = "Get runtime/build diagnostics", description = "Requires the DEVELOPER role's debug permission.")
    @PreAuthorize(Authorizations.DEBUG)
    @GetMapping("/info")
    public DebugInfoDto info() {
        return new DebugInfoDto(environment.getProperty("spring.application.name", "ledgerx-backend"),
                Arrays.asList(environment.getActiveProfiles()), System.getProperty("java.version"),
                ManagementFactory.getRuntimeMXBean().getUptime(), Instant.now());
    }
}
