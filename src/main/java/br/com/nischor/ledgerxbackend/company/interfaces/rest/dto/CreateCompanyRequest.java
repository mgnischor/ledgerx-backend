package br.com.nischor.ledgerxbackend.company.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidBrazilianState;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidBrazilianZipCode;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidCnpj;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST request payload for registering a new company, carrying its identification, size and
 * address data with bean validation constraints.
 *
 * @param legalName registered legal name of the company
 * @param tradeName trade (fantasy) name of the company
 * @param cnpj Brazilian CNPJ document number, validated by {@link ValidCnpj}
 * @param size company size classification
 * @param street street name of the company's address
 * @param number street number of the company's address
 * @param city city of the company's address
 * @param state Brazilian state (UF) of the company's address, validated by {@link ValidBrazilianState}
 * @param zipCode postal code (CEP) of the company's address, validated by {@link ValidBrazilianZipCode}
 * @param country country of the company's address
 */
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
