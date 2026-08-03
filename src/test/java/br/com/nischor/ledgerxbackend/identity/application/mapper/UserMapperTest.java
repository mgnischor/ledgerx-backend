package br.com.nischor.ledgerxbackend.identity.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void mapsAllFieldsFromDomainToDto() {
        var user = new User(UUID.randomUUID(), "Jane Doe", new EmailAddress("jane@example.com"), "hashed-password");
        user.grant(Role.MANAGER);

        var dto = mapper.toDto(user);

        assertThat(dto.id()).isEqualTo(user.getId());
        assertThat(dto.fullName()).isEqualTo("Jane Doe");
        assertThat(dto.email()).isEqualTo("jane@example.com");
        assertThat(dto.roles()).containsExactly(Role.MANAGER);
        assertThat(dto.active()).isTrue();
    }
}
