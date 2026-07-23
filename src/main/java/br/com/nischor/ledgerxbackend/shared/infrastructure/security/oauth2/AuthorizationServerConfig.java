package br.com.nischor.ledgerxbackend.shared.infrastructure.security.oauth2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * First-party OAuth2 Authorization Server exposing the standard {@code /oauth2/authorize},
 * {@code /oauth2/token}, {@code /oauth2/jwks} etc. endpoints (via
 * {@link OAuth2AuthorizationServerConfigurer}), restricted to the Authorization Code grant with
 * mandatory PKCE ({@link ClientSettings#isRequireProofKey()}). This is a separate, higher-priority
 * filter chain from the resource-server one in {@code SecurityConfig}: requests to
 * {@code /oauth2/**} are handled here (including the resource-owner login form), everything else
 * falls through to the Ed25519 JWT bearer-token rules.
 *
 * <p>Tokens issued here are signed with RSA (via the {@link JWKSource} bean below) rather than the
 * Ed25519 key used by {@code JwtService} for {@code /api/v1/auth/login}: the two token issuers are
 * intentionally independent, matching Spring Authorization Server's own key material handling.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(OAuth2AuthorizationServerProperties.class)
public class AuthorizationServerConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"),
                                authorizationServerConfigurer.getEndpointsMatcher()));
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(OAuth2AuthorizationServerProperties properties) {
        var clientSettings = ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(true)
                .build();

        var tokenSettings = TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(properties.getAccessTokenTtlSeconds()))
                .refreshTokenTimeToLive(Duration.ofSeconds(properties.getRefreshTokenTtlSeconds()))
                .reuseRefreshTokens(false)
                .build();

        var clientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(properties.getClientId())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings);
        properties.getRedirectUris().forEach(clientBuilder::redirectUri);
        properties.getScopes().forEach(clientBuilder::scope);

        return new InMemoryRegisteredClientRepository(clientBuilder.build());
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(OAuth2AuthorizationServerProperties properties) {
        return AuthorizationServerSettings.builder().issuer(properties.getIssuer()).build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private RSAKey generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA is not available in this JVM", e);
        }

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }
}
