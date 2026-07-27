package br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.repository;

import br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link UserJpaEntity}.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    /**
     * Looks up a user entity by email address.
     *
     * @param email the email address to search for.
     * @return the matching entity, or empty if none exists.
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * Checks whether a user entity with the given email address exists.
     *
     * @param email the email address to check.
     * @return {@code true} if a matching entity exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);
}
