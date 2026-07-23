package br.com.nischor.ledgerxbackend.identity.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.usecase.DeactivateUserUseCase;
import br.com.nischor.ledgerxbackend.identity.application.usecase.GrantRoleUseCase;
import br.com.nischor.ledgerxbackend.identity.application.usecase.RegisterUserUseCase;
import br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto.CreateUserRequest;
import br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto.GrantRoleRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GrantRoleUseCase grantRoleUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase, GrantRoleUseCase grantRoleUseCase,
            DeactivateUserUseCase deactivateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.grantRoleUseCase = grantRoleUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
    }

    /**
     * BR-001..BR-018: full name, email and password shape/uniqueness/strength rules are
     * enforced by {@link CreateUserRequest}'s bean validation constraints before this method
     * body runs.
     */
    @PostMapping
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserRequest request) {
        var dto = registerUserUseCase.execute(request.fullName(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * BR-020/BR-021: the target user must exist and the role must be one of the roles defined
     * by the {@code Role} enum (an unknown role value is rejected by Jackson before this method
     * runs, returning 400 Bad Request).
     */
    @PatchMapping("/{userId}/roles")
    public ResponseEntity<UserDto> grantRole(@PathVariable UUID userId, @Valid @RequestBody GrantRoleRequest request) {
        return ResponseEntity.ok(grantRoleUseCase.execute(userId, request.role()));
    }

    /** BR-023/BR-024: the target user must exist; deactivating twice is a no-op. */
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<UserDto> deactivate(@PathVariable UUID userId) {
        return ResponseEntity.ok(deactivateUserUseCase.execute(userId));
    }
}
