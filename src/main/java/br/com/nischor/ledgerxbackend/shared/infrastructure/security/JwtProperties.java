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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
