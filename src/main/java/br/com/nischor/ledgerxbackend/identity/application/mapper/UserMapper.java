package br.com.nischor.ledgerxbackend.identity.application.mapper;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link User} domain model and its outward-facing {@link UserDto}
 * representation.
 */
@Component
public class UserMapper {

    /**
     * Converts a domain {@link User} into a {@link UserDto}.
     *
     * @param user the domain user to convert.
     * @return the corresponding data transfer object.
     */
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFullName(), user.getEmail().value(), user.getRoles(),
                user.isActive());
    }
}
