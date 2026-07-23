package br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import jakarta.validation.constraints.NotNull;

public record GrantRoleRequest(@NotNull Role role) {
}
