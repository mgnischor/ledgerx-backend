package br.com.nischor.ledgerxbackend.identity.application.mapper;

import br.com.nischor.ledgerxbackend.identity.application.dto.UserDto;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFullName(), user.getEmail().value(), user.getRoles(),
                user.isActive());
    }
}
