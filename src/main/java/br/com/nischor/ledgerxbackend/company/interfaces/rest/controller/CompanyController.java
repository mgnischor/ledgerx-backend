package br.com.nischor.ledgerxbackend.company.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.usecase.DeactivateCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.application.usecase.RegisterCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.company.interfaces.rest.dto.CreateCompanyRequest;
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
    @PostMapping
    public ResponseEntity<CompanyDto> register(@Valid @RequestBody CreateCompanyRequest request) {
        var address = new Address(request.street(), request.number(), request.city(), request.state(),
                request.zipCode(), request.country());
        var dto = registerCompanyUseCase.execute(request.legalName(), request.tradeName(), request.cnpj(),
                request.size(), address);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** BR-042/BR-043: the target company must exist; deactivating twice is a no-op. */
    @PatchMapping("/{companyId}/deactivate")
    public ResponseEntity<CompanyDto> deactivate(@PathVariable UUID companyId) {
        return ResponseEntity.ok(deactivateCompanyUseCase.execute(companyId));
    }
}
