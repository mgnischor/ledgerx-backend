package br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for granting a role to an existing user.
 *
 * @param role the role to grant; must be a valid, non-null {@link Role}.
 */
public record GrantRoleRequest(@NotNull Role role) {
}
