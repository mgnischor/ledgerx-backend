package br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.FieldsNotEqual;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldsNotEqual(first = "email", second = "password", message = "password must not be equal to the email address")
public record CreateUserRequest(
        @NotBlank @Size(min = 2, max = 150) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 128) @StrongPassword String password) {
}
