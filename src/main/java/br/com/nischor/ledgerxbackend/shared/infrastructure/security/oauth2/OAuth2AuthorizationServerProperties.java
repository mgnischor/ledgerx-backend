package br.com.nischor.ledgerxbackend.shared.infrastructure.security.oauth2;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the first-party OAuth2 Authorization Code + PKCE flow served by this
 * application's own {@code /oauth2/*} endpoints (see {@code AuthorizationServerConfig}). This is
 * independent from the password-based {@code /api/v1/auth/login} Ed25519 JWT issued by
 * {@code JwtService}.
 */
@ConfigurationProperties(prefix = "ledgerx.security.oauth2")
public class OAuth2AuthorizationServerProperties {

    private String issuer = "https://localhost:8080";
    private String clientId = "ledgerx-spa";
    private List<String> redirectUris = List.of("http://127.0.0.1:8080/authorized");
    private List<String> scopes = List.of("api.read", "api.write");
    private long accessTokenTtlSeconds = 900;
    private long refreshTokenTtlSeconds = 86_400;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }
}
