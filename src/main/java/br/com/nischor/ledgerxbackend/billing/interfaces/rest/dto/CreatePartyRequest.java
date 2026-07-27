package br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber.DocumentType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidPartyDocument;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload to create a customer or supplier (party), validated so that the document
 * matches the declared document type via {@link ValidPartyDocument}.
 *
 * @param name display name of the party; must not be blank and at most 150 characters
 * @param documentType whether the document is a CPF or a CNPJ; must not be {@code null}
 * @param document raw document number (CPF or CNPJ); must not be blank
 * @param email contact email address; must not be blank and must be a valid email
 * @param type whether the party is a customer or a supplier; must not be {@code null}
 */
@ValidPartyDocument
public record CreatePartyRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull DocumentType documentType,
        @NotBlank String document,
        @NotBlank @Email String email,
        @NotNull PartyType type) {
}
