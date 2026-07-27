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

    /**
     * Returns the issuer URI advertised by the authorization server.
     *
     * @return the configured issuer URI
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the issuer URI advertised by the authorization server.
     *
     * @param issuer the issuer URI
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Returns the client identifier of the registered first-party client.
     *
     * @return the configured client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the client identifier of the registered first-party client.
     *
     * @param clientId the client id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Returns the allowed redirect URIs for the registered client.
     *
     * @return the configured redirect URIs
     */
    public List<String> getRedirectUris() {
        return redirectUris;
    }

    /**
     * Sets the allowed redirect URIs for the registered client.
     *
     * @param redirectUris the redirect URIs
     */
    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    /**
     * Returns the OAuth2 scopes granted to the registered client.
     *
     * @return the configured scopes
     */
    public List<String> getScopes() {
        return scopes;
    }

    /**
     * Sets the OAuth2 scopes granted to the registered client.
     *
     * @param scopes the scopes
     */
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    /**
     * Returns the access token time-to-live, in seconds.
     *
     * @return the configured access token TTL in seconds
     */
    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    /**
     * Sets the access token time-to-live, in seconds.
     *
     * @param accessTokenTtlSeconds the access token TTL in seconds
     */
    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    /**
     * Returns the refresh token time-to-live, in seconds.
     *
     * @return the configured refresh token TTL in seconds
     */
    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    /**
     * Sets the refresh token time-to-live, in seconds.
     *
     * @param refreshTokenTtlSeconds the refresh token TTL in seconds
     */
    public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }
}
