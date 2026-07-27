package br.com.nischor.ledgerxbackend.identity.application.dto;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import java.util.Set;
import java.util.UUID;

/**
 * Read-facing representation of a {@link br.com.nischor.ledgerxbackend.identity.domain.model.User},
 * exposed by the application layer to controllers and other outward-facing consumers.
 *
 * @param id        the user's unique identifier.
 * @param fullName  the user's full name.
 * @param email     the user's email address, as plain text.
 * @param roles     the roles currently granted to the user.
 * @param active    whether the user account is active.
 */
public record UserDto(UUID id, String fullName, String email, Set<Role> roles, boolean active) {
}
