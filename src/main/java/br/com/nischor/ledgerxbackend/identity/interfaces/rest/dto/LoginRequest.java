package br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for authenticating with email and password.
 *
 * @param email    the account's email address.
 * @param password the account's plain-text password.
 */
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
}
