package br.com.nischor.ledgerxbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LedgerxBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LedgerxBackendApplication.class, args);
    }

}
