package br.com.nischor.ledgerxbackend.identity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.identity.domain.exception.InvalidCredentialsException;
import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtProperties;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    private static final String RAW_PASSWORD = "correct-horse-battery-staple";
    private static final String HASHED_PASSWORD = "hashed-password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private LoginUseCase loginUseCase;
    private User activeUser;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setExpirationSeconds(1800);
        loginUseCase = new LoginUseCase(userRepository, passwordEncoder, jwtService, jwtProperties);

        activeUser = new User(UUID.randomUUID(), "Jane Doe", new EmailAddress("jane@example.com"), HASHED_PASSWORD);
        activeUser.grant(Role.ADMIN);
    }

    @Test
    void issuesTokenForValidCredentials() {
        when(userRepository.findByEmail(new EmailAddress("jane@example.com"))).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(jwtService.issue("jane@example.com", Set.of("ADMIN"))).thenReturn("signed-token");

        var result = loginUseCase.execute("jane@example.com", RAW_PASSWORD);

        assertThat(result.accessToken()).isEqualTo("signed-token");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.expiresInSeconds()).isEqualTo(1800);
    }

    @Test
    void rejectsUnknownEmail() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute("nobody@example.com", RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsWrongPassword() {
        when(userRepository.findByEmail(new EmailAddress("jane@example.com"))).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrong-password", HASHED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute("jane@example.com", "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsDeactivatedUser() {
        activeUser.deactivate();
        when(userRepository.findByEmail(new EmailAddress("jane@example.com"))).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> loginUseCase.execute("jane@example.com", RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
