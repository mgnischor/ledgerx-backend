package br.com.nischor.ledgerxbackend.identity.application.usecase;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.application.mapper.UserMapper;
import br.com.nischor.ledgerxbackend.identity.domain.event.UserRegisteredEvent;
import br.com.nischor.ledgerxbackend.identity.domain.exception.EmailAlreadyRegisteredException;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.event.DomainEventPublisher;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final DomainEventPublisher eventPublisher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
            DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    public UserDto execute(String fullName, String rawEmail, String rawPassword) {
        var email = new EmailAddress(rawEmail);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(rawEmail);
        }

        var user = new User(UUID.randomUUID(), fullName, email, passwordEncoder.encode(rawPassword));
        var saved = userRepository.save(user);
        eventPublisher.publish(new UserRegisteredEvent(saved.getId(), email.value()));

        return userMapper.toDto(saved);
    }
}
