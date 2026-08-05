package br.com.nischor.ledgerxbackend.company.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.usecase.DeactivateCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.application.usecase.ListCompaniesUseCase;
import br.com.nischor.ledgerxbackend.company.application.usecase.RegisterCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.company.interfaces.rest.dto.CreateCompanyRequest;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints to register and deactivate companies (tenants) under
 * {@code /api/v1/companies}.
 */
@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Companies", description = "Company (tenant) registration and lifecycle")
public class CompanyController {

    private final RegisterCompanyUseCase registerCompanyUseCase;
    private final DeactivateCompanyUseCase deactivateCompanyUseCase;
    private final ListCompaniesUseCase listCompaniesUseCase;

    /**
     * Creates the controller with its required use cases.
     *
     * @param registerCompanyUseCase use case handling company registration
     * @param deactivateCompanyUseCase use case handling company deactivation
     * @param listCompaniesUseCase use case handling company listing
     */
    public CompanyController(RegisterCompanyUseCase registerCompanyUseCase,
            DeactivateCompanyUseCase deactivateCompanyUseCase, ListCompaniesUseCase listCompaniesUseCase) {
        this.registerCompanyUseCase = registerCompanyUseCase;
        this.deactivateCompanyUseCase = deactivateCompanyUseCase;
        this.listCompaniesUseCase = listCompaniesUseCase;
    }

    /**
     * Handles {@code GET /api/v1/companies} to list every registered company.
     *
     * <p>Companies have no per-user membership: any caller with read access sees every company,
     * so this is how the frontend gives an authorized user (e.g. a DEVELOPER/ADMINISTRATOR)
     * universal access across all tenants.
     *
     * @return a {@code 200 OK} response with every company as a {@link CompanyDto}
     */
    @Operation(summary = "List all companies")
    @ApiResponse(responseCode = "200", description = "Companies listed")
    @PreAuthorize(Authorizations.READ)
    @GetMapping
    public ResponseEntity<List<CompanyDto>> list() {
        return ResponseEntity.ok(listCompaniesUseCase.execute());
    }

    /**
     * Handles {@code POST /api/v1/companies} to register a new company.
     *
     * <p>BR-023..BR-035: legal name/trade name/address shape, CNPJ format+check-digit+uniqueness
     * and company size enum rules are enforced by {@link CreateCompanyRequest}'s bean
     * validation constraints and {@code RegisterCompanyUseCase} before this method body runs.
     *
     * @param request validated request body with the company's registration data
     * @return a {@code 201 Created} response with the registered company as a {@link CompanyDto}
     */
    @Operation(summary = "Register a new company",
            description = "Validates CNPJ check digits, Brazilian UF and CEP format. BR-023..BR-035.")
    @ApiResponse(responseCode = "201", description = "Company created")
    @ApiResponse(responseCode = "400", description = "Validation failure (invalid CNPJ/UF/CEP, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "CNPJ already registered",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<CompanyDto> register(@Valid @RequestBody CreateCompanyRequest request) {
        var address = new Address(request.street(), request.number(), request.city(), request.state(),
                request.zipCode(), request.country());
        var dto = registerCompanyUseCase.execute(request.legalName(), request.tradeName(), request.cnpj(),
                request.size(), address);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Handles {@code PATCH /api/v1/companies/{companyId}/deactivate} to deactivate a company.
     *
     * <p>BR-036/BR-037: the target company must exist; deactivating twice is a no-op.
     *
     * @param companyId identifier of the company to deactivate, taken from the path
     * @return a {@code 200 OK} response with the deactivated company as a {@link CompanyDto}
     */
    @Operation(summary = "Deactivate a company", description = "Idempotent. BR-036/BR-037.")
    @ApiResponse(responseCode = "200", description = "Company deactivated")
    @ApiResponse(responseCode = "404", description = "Company not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.DELETE)
    @PatchMapping("/{companyId}/deactivate")
    public ResponseEntity<CompanyDto> deactivate(@PathVariable UUID companyId) {
        return ResponseEntity.ok(deactivateCompanyUseCase.execute(companyId));
    }
}
