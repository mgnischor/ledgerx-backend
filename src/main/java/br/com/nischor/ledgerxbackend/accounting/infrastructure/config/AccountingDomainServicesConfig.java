package br.com.nischor.ledgerxbackend.accounting.infrastructure.config;

import br.com.nischor.ledgerxbackend.accounting.domain.service.AccountBalanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that registers accounting domain services as beans.
 */
@Configuration
public class AccountingDomainServicesConfig {

    /**
     * Creates the {@link AccountBalanceService} bean.
     *
     * @return a new {@link AccountBalanceService} instance
     */
    @Bean
    public AccountBalanceService accountBalanceService() {
        return new AccountBalanceService();
    }
}
