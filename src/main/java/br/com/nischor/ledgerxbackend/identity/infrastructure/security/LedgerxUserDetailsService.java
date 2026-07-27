package br.com.nischor.ledgerxbackend.identity.infrastructure.security;

import br.com.nischor.ledgerxbackend.identity.domain.model.RolePermissions;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.stream.Stream;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges the {@code identity} domain model to Spring Security's {@link UserDetailsService},
 * used by the Authorization Server's resource-owner login form (see {@code AuthorizationServerConfig})
 * to authenticate the user before an authorization code/consent is issued.
 */
@Service
public class LedgerxUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Creates the service.
     *
     * @param userRepository the repository used to look up the user by email.
     */
    public LedgerxUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a {@link UserDetails} for the given email, exposing {@code ROLE_*} authorities for
     * each granted {@code Role} and {@code PERMISSION_*} authorities for each permission derived
     * from those roles via {@link RolePermissions}.
     *
     * @param email the username, which is the user's email address.
     * @return the corresponding Spring Security user details.
     * @throws UsernameNotFoundException if no user exists for the given email, or the email is
     *                                    not a valid {@link EmailAddress}.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user;
        try {
            user = userRepository.findByEmail(new EmailAddress(email))
                    .orElseThrow(() -> new UsernameNotFoundException("No user found for email: %s".formatted(email)));
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid email: %s".formatted(email), e);
        }

        String[] authorities = Stream
                .concat(user.getRoles().stream().map(role -> "ROLE_" + role),
                        RolePermissions.of(user.getRoles()).stream().map(permission -> "PERMISSION_" + permission))
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail().value())
                .password(user.getHashedPassword())
                .disabled(!user.isActive())
                .authorities(authorities)
                .build();
    }
}
