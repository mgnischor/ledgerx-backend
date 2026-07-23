package br.com.nischor.ledgerxbackend.company.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidBrazilianState;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidBrazilianZipCode;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidCnpj;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(
        @NotBlank @Size(max = 150) String legalName,
        @NotBlank @Size(max = 150) String tradeName,
        @NotBlank @ValidCnpj String cnpj,
        @NotNull CompanySize size,
        @NotBlank @Size(max = 150) String street,
        @NotBlank @Size(max = 20) String number,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @ValidBrazilianState String state,
        @NotBlank @ValidBrazilianZipCode String zipCode,
        @NotBlank @Size(max = 60) String country) {
}
