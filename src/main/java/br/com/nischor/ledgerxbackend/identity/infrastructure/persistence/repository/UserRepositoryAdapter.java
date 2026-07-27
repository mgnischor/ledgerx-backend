package br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.mapper.UserJpaMapper;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * {@link UserRepository} implementation backed by Spring Data JPA, delegating persistence to
 * {@link UserJpaRepository} and model conversion to {@link UserJpaMapper}.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserJpaMapper mapper;

    /**
     * Creates the adapter.
     *
     * @param jpaRepository the Spring Data repository used for persistence.
     * @param mapper        the mapper used to convert between domain and entity models.
     */
    public UserRepositoryAdapter(UserJpaRepository jpaRepository, UserJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     *
     * @param user the user to save.
     * @return the persisted user.
     */
    @Override
    public User save(User user) {
        var saved = jpaRepository.save(mapper.toEntity(user));
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     *
     * @param id the user's identifier.
     * @return the matching user, or empty if none exists.
     */
    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     *
     * @param email the user's email address.
     * @return the matching user, or empty if none exists.
     */
    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return jpaRepository.findByEmail(email.value()).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     *
     * @param email the email address to check.
     * @return {@code true} if a user with that email exists, {@code false} otherwise.
     */
    @Override
    public boolean existsByEmail(EmailAddress email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
