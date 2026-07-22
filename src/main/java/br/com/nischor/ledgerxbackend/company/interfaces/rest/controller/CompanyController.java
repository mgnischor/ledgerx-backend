package br.com.nischor.ledgerxbackend.company.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.usecase.RegisterCompanyUseCase;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.company.interfaces.rest.dto.CreateCompanyRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final RegisterCompanyUseCase registerCompanyUseCase;

    public CompanyController(RegisterCompanyUseCase registerCompanyUseCase) {
        this.registerCompanyUseCase = registerCompanyUseCase;
    }

    @PostMapping
    public ResponseEntity<CompanyDto> register(@Valid @RequestBody CreateCompanyRequest request) {
        var address = new Address(request.street(), request.number(), request.city(), request.state(),
                request.zipCode(), request.country());
        var dto = registerCompanyUseCase.execute(request.legalName(), request.tradeName(), request.cnpj(),
                request.size(), address);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
