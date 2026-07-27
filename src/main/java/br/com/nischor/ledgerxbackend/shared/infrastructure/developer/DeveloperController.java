package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import br.com.nischor.ledgerxbackend.identity.domain.model.RolePermissions;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes a full compendium of the current container's runtime environment: operating system,
 * CPU, memory, storage, dependent-service versions (RabbitMQ, PostgreSQL, Grafana) and Java
 * runtime details. Restricted to the {@code DEVELOPER} role, the only role granted the
 * {@code PERMISSION_DEBUG} authority; see {@link RolePermissions}.
 */
@RestController
@RequestMapping("/api/v1/developer")
@Tag(name = "Developer", description = "Developer-only runtime environment diagnostics")
public class DeveloperController {

    private final DeveloperInfoService developerInfoService;

    /**
     * Creates a new controller backed by the given {@link DeveloperInfoService}.
     *
     * @param developerInfoService the service used to collect runtime environment diagnostics
     */
    public DeveloperController(DeveloperInfoService developerInfoService) {
        this.developerInfoService = developerInfoService;
    }

    /**
     * Returns a full snapshot of the current container's runtime environment: operating system,
     * CPU, memory, storage, the live versions of RabbitMQ/PostgreSQL/Grafana, and Java runtime
     * details. Requires the {@code PERMISSION_DEBUG} authority (granted to the {@code DEVELOPER}
     * role).
     *
     * @return the assembled {@link DeveloperInfoDto}
     */
    @Operation(summary = "Get runtime environment diagnostics",
            description = "Requires the DEVELOPER role's debug permission.")
    @PreAuthorize(Authorizations.DEBUG)
    @GetMapping
    public DeveloperInfoDto info() {
        return developerInfoService.collect();
    }
}
