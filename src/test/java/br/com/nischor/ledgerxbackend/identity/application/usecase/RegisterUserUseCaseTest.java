package br.com.nischor.ledgerxbackend.identity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.mapper.UserMapper;
import br.com.nischor.ledgerxbackend.identity.domain.event.UserRegisteredEvent;
import br.com.nischor.ledgerxbackend.identity.domain.exception.EmailAlreadyRegisteredException;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userRepository, passwordEncoder, userMapper, eventPublisher);
    }

    @Test
    void registersUserWithHashedPasswordAndPublishesEvent() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("Str0ng!Pass")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new UserDto(UUID.randomUUID(), "Jane Doe", "jane@example.com", Set.of(), true);
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        var result = useCase.execute("Jane Doe", "jane@example.com", "Str0ng!Pass");

        assertThat(result).isEqualTo(dto);
        verify(eventPublisher).publish(any(UserRegisteredEvent.class));
    }

    @Test
    void rejectsAlreadyRegisteredEmail() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("Jane Doe", "jane@example.com", "Str0ng!Pass"))
                .isInstanceOf(EmailAlreadyRegisteredException.class);
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }
}
