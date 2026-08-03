package br.com.nischor.ledgerxbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the LedgerX Backend Spring Boot application.
 *
 * <p>Bootstraps the Spring application context. JPA auditing is enabled separately by
 * {@link br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.JpaAuditingConfig}.
 */
@SpringBootApplication
public class LedgerxBackendApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(LedgerxBackendApplication.class, args);
    }

}
