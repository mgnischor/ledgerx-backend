package br.com.nischor.ledgerxbackend.shared.infrastructure.config;

import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtAuthenticationFilter;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Default (lowest-priority) filter chain, evaluated after
 * {@code AuthorizationServerConfig}'s {@code /oauth2/**} chain. Permits public access to the
 * OpenAPI/Swagger documentation, user registration and the password-login endpoint. Every other
 * API request must carry a valid Ed25519-signed (EdDSA) JWT bearer token, verified by
 * {@link JwtAuthenticationFilter}.
 *
 * <p>Session creation is {@code IF_REQUIRED} rather than fully stateless: the API itself never
 * relies on a session (bearer tokens are re-verified on every request), but the resource-owner
 * login form used by the OAuth2/PKCE authorization-code flow needs one to keep the user
 * authenticated between {@code GET /oauth2/authorize} and their consent decision.
 */
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_DOC_PATHS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**"};
    private static final String[] PUBLIC_AUTH_PATHS = {"/api/v1/auth/login"};

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_DOC_PATHS).permitAll()
                        .requestMatchers(PUBLIC_AUTH_PATHS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
