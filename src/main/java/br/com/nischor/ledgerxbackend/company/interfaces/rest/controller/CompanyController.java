package br.com.nischor.ledgerxbackend.company.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.usecase.DeactivateCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.application.usecase.RegisterCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.company.interfaces.rest.dto.CreateCompanyRequest;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Companies", description = "Company (tenant) registration and lifecycle")
public class CompanyController {

    private final RegisterCompanyUseCase registerCompanyUseCase;
    private final DeactivateCompanyUseCase deactivateCompanyUseCase;

    public CompanyController(RegisterCompanyUseCase registerCompanyUseCase,
            DeactivateCompanyUseCase deactivateCompanyUseCase) {
        this.registerCompanyUseCase = registerCompanyUseCase;
        this.deactivateCompanyUseCase = deactivateCompanyUseCase;
    }

    /**
     * BR-026..BR-040: legal name/trade name/address shape, CNPJ format+check-digit+uniqueness
     * and company size enum rules are enforced by {@link CreateCompanyRequest}'s bean
     * validation constraints and {@code RegisterCompanyUseCase} before this method body runs.
     */
    @Operation(summary = "Register a new company",
            description = "Validates CNPJ check digits, Brazilian UF and CEP format. BR-026..BR-040.")
    @ApiResponse(responseCode = "201", description = "Company created")
    @ApiResponse(responseCode = "400", description = "Validation failure (invalid CNPJ/UF/CEP, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "CNPJ already registered",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<CompanyDto> register(@Valid @RequestBody CreateCompanyRequest request) {
        var address = new Address(request.street(), request.number(), request.city(), request.state(),
                request.zipCode(), request.country());
        var dto = registerCompanyUseCase.execute(request.legalName(), request.tradeName(), request.cnpj(),
                request.size(), address);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** BR-042/BR-043: the target company must exist; deactivating twice is a no-op. */
    @Operation(summary = "Deactivate a company", description = "Idempotent. BR-042/BR-043.")
    @ApiResponse(responseCode = "200", description = "Company deactivated")
    @ApiResponse(responseCode = "404", description = "Company not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PatchMapping("/{companyId}/deactivate")
    public ResponseEntity<CompanyDto> deactivate(@PathVariable UUID companyId) {
        return ResponseEntity.ok(deactivateCompanyUseCase.execute(companyId));
    }
}
