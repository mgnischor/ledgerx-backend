package br.com.nischor.ledgerxbackend.identity.infrastructure.bootstrap;

import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures at least one DEVELOPER account exists so the API is never in a dead-end state where
 * nobody can grant roles (see {@code GrantRoleUseCase}'s {@code Authorizations.FULL_ACCESS}
 * check) or manage other users. Runs on every startup but is idempotent: it only creates the
 * account if {@code ledgerx.security.bootstrap-admin.email} is not already registered.
 *
 * <p>The default email/password are for local development only — override
 * {@code ledgerx.security.bootstrap-admin.*} (or disable with {@code .enabled=false}) in any
 * shared/production environment.
 */
@Component
@ConditionalOnProperty(value = "ledgerx.security.bootstrap-admin.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AdminBootstrapProperties.class)
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminBootstrapProperties properties;

    public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AdminBootstrapProperties properties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        var email = new EmailAddress(properties.getEmail());
        if (userRepository.existsByEmail(email)) {
            return;
        }

        var admin = new User(UUID.randomUUID(), properties.getFullName(), email,
                passwordEncoder.encode(properties.getPassword()));
        admin.grant(Role.DEVELOPER);
        userRepository.save(admin);

        log.warn("Bootstrapped DEVELOPER account {} with the configured/default password. "
                + "Change it (or set ledgerx.security.bootstrap-admin.password) before deploying anywhere "
                + "other than local development.", email.value());
    }
}
