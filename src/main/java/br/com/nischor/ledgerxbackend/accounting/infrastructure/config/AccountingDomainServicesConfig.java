package br.com.nischor.ledgerxbackend.accounting.infrastructure.config;

import br.com.nischor.ledgerxbackend.accounting.domain.service.AccountBalanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountingDomainServicesConfig {

    @Bean
    public AccountBalanceService accountBalanceService() {
        return new AccountBalanceService();
    }
}
