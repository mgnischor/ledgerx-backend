package br.com.nischor.ledgerxbackend.identity.application.dto;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import java.util.Set;
import java.util.UUID;

public record UserDto(UUID id, String fullName, String email, Set<Role> roles, boolean active) {
}
