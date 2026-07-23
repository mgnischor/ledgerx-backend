package br.com.nischor.ledgerxbackend.identity.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.identity.application.dto.AuthenticationResultDto;
import br.com.nischor.ledgerxbackend.identity.application.usecase.LoginUseCase;
import br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto.LoginRequest;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and Ed25519-signed JWT issuance")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @Operation(summary = "Authenticate with email and password",
            description = "Returns an Ed25519-signed (EdDSA) JWT access token to use as a Bearer credential.")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid email or password",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResultDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.execute(request.email(), request.password()));
    }
}
