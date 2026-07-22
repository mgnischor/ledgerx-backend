package br.com.nischor.ledgerxbackend.identity.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.usecase.RegisterUserUseCase;
import br.com.nischor.ledgerxbackend.identity.interfaces.rest.dto.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserRequest request) {
        var dto = registerUserUseCase.execute(request.fullName(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
