package br.com.nischor.ledgerxbackend.identity.infrastructure.security;

import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
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

    public LedgerxUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user;
        try {
            user = userRepository.findByEmail(new EmailAddress(email))
                    .orElseThrow(() -> new UsernameNotFoundException("No user found for email: %s".formatted(email)));
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid email: %s".formatted(email), e);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail().value())
                .password(user.getHashedPassword())
                .disabled(!user.isActive())
                .authorities(user.getRoles().stream().map(role -> "ROLE_" + role).toArray(String[]::new))
                .build();
    }
}
