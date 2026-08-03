package br.com.nischor.ledgerxbackend.identity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.mapper.UserMapper;
import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrantRoleUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private GrantRoleUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new GrantRoleUseCase(userRepository, userMapper);
    }

    @Test
    void grantsRoleToExistingUser() {
        var user = new User(userId, "Jane Doe", new EmailAddress("jane@example.com"), "hashed-password");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        var dto = new UserDto(userId, "Jane Doe", "jane@example.com", Set.of(Role.MANAGER), true);
        when(userMapper.toDto(user)).thenReturn(dto);

        var result = useCase.execute(userId, Role.MANAGER);

        assertThat(result.roles()).contains(Role.MANAGER);
        assertThat(user.getRoles()).contains(Role.MANAGER);
    }

    @Test
    void rejectsUnknownUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, Role.MANAGER)).isInstanceOf(EntityNotFoundException.class);
    }
}
