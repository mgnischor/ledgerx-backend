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

@Service
public class GrantRoleUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GrantRoleUseCase(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PreAuthorize(Authorizations.FULL_ACCESS)
    public UserDto execute(UUID userId, Role role) {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));
        user.grant(role);
        return userMapper.toDto(userRepository.save(user));
    }
}
