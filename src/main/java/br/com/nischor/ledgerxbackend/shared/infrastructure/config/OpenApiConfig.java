package br.com.nischor.ledgerxbackend.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ledgerxOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("LedgerX Backend API")
                        .description("Open-source financial management API for micro and small businesses: "
                                + "cash accounts, income/expense tracking, accounts receivable/payable and "
                                + "cash-flow reporting.")
                        .version("v1")
                        .contact(new Contact().name("LedgerX").url("https://github.com/nischor/ledgerx-backend"))
                        .license(new License().name("MIT")));
    }
}
