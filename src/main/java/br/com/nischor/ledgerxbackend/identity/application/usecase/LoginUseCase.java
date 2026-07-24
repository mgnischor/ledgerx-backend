package br.com.nischor.ledgerxbackend.identity.application.usecase;

import br.com.nischor.ledgerxbackend.identity.application.dto.AuthenticationResultDto;
import br.com.nischor.ledgerxbackend.identity.domain.exception.InvalidCredentialsException;
import br.com.nischor.ledgerxbackend.identity.domain.model.RolePermissions;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtProperties;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public AuthenticationResultDto execute(String rawEmail, String rawPassword) {
        var email = new EmailAddress(rawEmail);
        var user = userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordEncoder.matches(rawPassword, user.getHashedPassword())) {
            throw new InvalidCredentialsException();
        }

        var roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        var permissions = RolePermissions.of(user.getRoles()).stream().map(Enum::name).collect(Collectors.toSet());
        var accessToken = jwtService.issue(user.getEmail().value(), roles, permissions);
        return new AuthenticationResultDto(accessToken, TOKEN_TYPE, jwtProperties.getExpirationSeconds());
    }
}
