package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import java.util.List;

/**
 * Decoded, signature-verified claims of a JWT access token. {@code permissions} is embedded by
 * the issuer (see {@code LoginUseCase}) rather than recomputed from {@code roles} here, so this
 * package does not need to depend on the {@code identity} module's {@code RolePermissions}.
 *
 * @param subject     the JWT subject, typically the authenticated user's identifier
 * @param roles       the roles embedded in the token
 * @param permissions the permissions embedded in the token
 */
public record JwtClaims(String subject, List<String> roles, List<String> permissions) {
}
