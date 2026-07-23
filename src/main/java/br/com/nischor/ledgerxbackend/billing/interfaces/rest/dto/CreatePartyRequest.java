package br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber.DocumentType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.ValidPartyDocument;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidPartyDocument
public record CreatePartyRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull DocumentType documentType,
        @NotBlank String document,
        @NotBlank @Email String email,
        @NotNull PartyType type) {
}
