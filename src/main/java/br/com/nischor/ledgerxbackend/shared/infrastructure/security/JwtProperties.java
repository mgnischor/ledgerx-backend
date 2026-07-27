package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for Ed25519-signed JWT access tokens. {@code privateKey}/{@code publicKey} are
 * Base64-encoded DER keys (PKCS#8 for the private key, X.509/SPKI for the public key). When left
 * blank, {@link JwtKeyConfig} generates an ephemeral in-memory key pair, which is fine for local
 * development but means tokens no longer validate across restarts or multiple instances.
 */
@ConfigurationProperties(prefix = "ledgerx.security.jwt")
public class JwtProperties {

    private String issuer = "ledgerx-backend";
    private long expirationSeconds = 3600;
    private String privateKey = "";
    private String publicKey = "";

    /**
     * Returns the JWT issuer claim value.
     *
     * @return the configured issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the JWT issuer claim value.
     *
     * @param issuer the issuer to embed in issued tokens and validate on verification
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Returns the token expiration time, in seconds from issuance.
     *
     * @return the configured expiration time in seconds
     */
    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    /**
     * Sets the token expiration time, in seconds from issuance.
     *
     * @param expirationSeconds the expiration time in seconds
     */
    public void setExpirationSeconds(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Returns the configured Base64-encoded PKCS#8 Ed25519 private key.
     *
     * @return the configured private key, or an empty string if not set
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Sets the Base64-encoded PKCS#8 Ed25519 private key.
     *
     * @param privateKey the Base64-encoded private key
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Returns the configured Base64-encoded X.509/SPKI Ed25519 public key.
     *
     * @return the configured public key, or an empty string if not set
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Sets the Base64-encoded X.509/SPKI Ed25519 public key.
     *
     * @param publicKey the Base64-encoded public key
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
