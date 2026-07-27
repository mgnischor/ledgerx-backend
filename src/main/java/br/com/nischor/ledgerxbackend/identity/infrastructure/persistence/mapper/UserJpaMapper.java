package br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.entity.UserJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link User} domain model and its {@link UserJpaEntity} persistence
 * representation.
 */
@Component
public class UserJpaMapper {

    /**
     * Converts a persistence entity into the domain model.
     *
     * @param entity the JPA entity to convert.
     * @return the corresponding domain user, with roles and active state applied.
     */
    public User toDomain(UserJpaEntity entity) {
        var user = new User(entity.getId(), entity.getFullName(), new EmailAddress(entity.getEmail()),
                entity.getHashedPassword());
        entity.getRoles().forEach(user::grant);
        if (!entity.isActive()) {
            user.deactivate();
        }
        return user;
    }

    /**
     * Converts a domain user into its persistence representation.
     *
     * @param user the domain user to convert.
     * @return the corresponding JPA entity, with roles and active state applied.
     */
    public UserJpaEntity toEntity(User user) {
        var entity = new UserJpaEntity(user.getId(), user.getFullName(), user.getEmail().value(),
                user.getHashedPassword());
        entity.getRoles().addAll(user.getRoles());
        entity.setActive(user.isActive());
        return entity;
    }
}
