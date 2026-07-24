package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import java.util.List;

/**
 * Decoded, signature-verified claims of a JWT access token. {@code permissions} is embedded by
 * the issuer (see {@code LoginUseCase}) rather than recomputed from {@code roles} here, so this
 * package does not need to depend on the {@code identity} module's {@code RolePermissions}.
 */
public record JwtClaims(String subject, List<String> roles, List<String> permissions) {
}
