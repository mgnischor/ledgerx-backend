package br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.infrastructure.persistence.entity.UserJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper {

    public User toDomain(UserJpaEntity entity) {
        var user = new User(entity.getId(), entity.getFullName(), new EmailAddress(entity.getEmail()),
                entity.getHashedPassword());
        entity.getRoles().forEach(user::grant);
        if (!entity.isActive()) {
            user.deactivate();
        }
        return user;
    }

    public UserJpaEntity toEntity(User user) {
        var entity = new UserJpaEntity(user.getFullName(), user.getEmail().value(), user.getHashedPassword());
        entity.getRoles().addAll(user.getRoles());
        entity.setActive(user.isActive());
        return entity;
    }
}
