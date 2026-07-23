package br.com.nischor.ledgerxbackend.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Permits public access to the OpenAPI/Swagger documentation so it can be browsed without
 * authentication. No authentication provider is configured yet for the rest of the API (see
 * README "Known gaps"), so every other request still falls through to Spring Security's
 * defaults.
 */
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_DOC_PATHS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/api/v1/users"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PUBLIC_DOC_PATHS).permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
