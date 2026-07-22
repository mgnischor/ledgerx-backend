package br.com.nischor.ledgerxbackend.company.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
        @NotBlank String legalName,
        @NotBlank String tradeName,
        @NotBlank String cnpj,
        CompanySize size,
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zipCode,
        @NotBlank String country) {
}
