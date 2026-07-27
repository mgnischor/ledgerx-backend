package br.com.nischor.ledgerxbackend.identity.application.usecase;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.mapper.UserMapper;
import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Grants a {@link Role} to an existing user. Only callable by full-access roles.
 */
@Service
public class GrantRoleUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Creates the use case.
     *
     * @param userRepository the repository used to load and persist users.
     * @param userMapper     the mapper used to convert the updated user into a {@link UserDto}.
     */
    public GrantRoleUseCase(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Grants {@code role} to the user identified by {@code userId}.
     *
     * @param userId the identifier of the user to update.
     * @param role   the role to grant.
     * @return the updated user, as a {@link UserDto}.
     * @throws EntityNotFoundException if no user exists with the given identifier.
     */
    @PreAuthorize(Authorizations.FULL_ACCESS)
    public UserDto execute(UUID userId, Role role) {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));
        user.grant(role);
        return userMapper.toDto(userRepository.save(user));
    }
}
