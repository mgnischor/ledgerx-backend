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

/**
 * Authenticates a user by email and password and issues a signed JWT access token on success.
 */
@Service
public class LoginUseCase {

    /** HTTP authorization scheme reported alongside the issued access token. */
    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * Creates the use case.
     *
     * @param userRepository  the repository used to look up the user by email.
     * @param passwordEncoder the encoder used to verify the supplied password against the stored hash.
     * @param jwtService      the service used to issue the signed access token.
     * @param jwtProperties   the configuration providing the token's time to live.
     */
    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Authenticates a user with the given credentials and issues an access token.
     *
     * @param rawEmail    the plain-text email address supplied by the caller.
     * @param rawPassword the plain-text password supplied by the caller.
     * @return the issued access token and related metadata.
     * @throws InvalidCredentialsException if no matching user exists, the account is inactive, or
     *                                     the password does not match.
     */
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
