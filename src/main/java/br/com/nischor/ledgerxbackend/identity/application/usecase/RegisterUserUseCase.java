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

/**
 * Registers a new user account, hashing the supplied password and publishing a
 * {@link UserRegisteredEvent} once the account is persisted.
 */
@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final DomainEventPublisher eventPublisher;

    /**
     * Creates the use case.
     *
     * @param userRepository  the repository used to check for existing emails and persist the new user.
     * @param passwordEncoder the encoder used to hash the raw password before storage.
     * @param userMapper      the mapper used to convert the saved user into a {@link UserDto}.
     * @param eventPublisher  the publisher used to broadcast the {@link UserRegisteredEvent}.
     */
    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
            DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new user with the given attributes.
     *
     * @param fullName    the user's full name.
     * @param rawEmail    the plain-text email address to register.
     * @param rawPassword the plain-text password to hash and store.
     * @return the newly created user, as a {@link UserDto}.
     * @throws EmailAlreadyRegisteredException if a user with the given email already exists.
     */
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
