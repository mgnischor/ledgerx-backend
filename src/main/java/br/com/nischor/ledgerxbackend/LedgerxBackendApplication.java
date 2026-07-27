package br.com.nischor.ledgerxbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the LedgerX Backend Spring Boot application.
 *
 * <p>Bootstraps the Spring application context and enables JPA auditing
 * (e.g. automatic population of created/modified timestamps on entities).
 */
@SpringBootApplication
@EnableJpaAuditing
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
