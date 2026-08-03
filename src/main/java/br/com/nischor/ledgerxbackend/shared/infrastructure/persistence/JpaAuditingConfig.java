package br.com.nischor.ledgerxbackend.shared.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing (e.g. automatic population of created/modified timestamps on entities).
 *
 * <p>Kept separate from {@code LedgerxBackendApplication} so that {@code @WebMvcTest} slices, which use the
 * application class only as a component-scan anchor, do not attempt to initialize JPA auditing infrastructure
 * without a real {@code EntityManagerFactory} in the context.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
