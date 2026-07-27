package br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.FieldsNotEqual;
import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for registering a new user. Validated against BR-001..BR-018: full name and
 * email shape/size constraints, a strong password policy, and a password that must differ from
 * the email address.
 *
 * @param fullName the new user's full name (2-150 characters).
 * @param email    the new user's email address (valid format, up to 254 characters).
 * @param password the new user's plain-text password (8-128 characters, must satisfy the strong
 *                 password policy and must not equal the email address).
 */
@FieldsNotEqual(first = "email", second = "password", message = "password must not be equal to the email address")
public record CreateUserRequest(
        @NotBlank @Size(min = 2, max = 150) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 128) @StrongPassword String password) {
}
