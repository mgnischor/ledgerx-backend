package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the Ed25519 {@link KeyPair} used to sign and verify JWTs. If
 * {@code ledgerx.security.jwt.private-key}/{@code public-key} are configured, the DER-encoded,
 * Base64 keys are loaded; otherwise a fresh key pair is generated at startup, mirroring the
 * graceful-fallback pattern used by {@link PasswordEncoderConfig}.
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtKeyConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtKeyConfig.class);
    private static final String ALGORITHM = "Ed25519";

    @Bean
    public KeyPair jwtSigningKeyPair(JwtProperties properties) {
        if (!properties.getPrivateKey().isBlank() && !properties.getPublicKey().isBlank()) {
            return loadConfiguredKeyPair(properties);
        }

        log.warn("No Ed25519 JWT signing key configured (ledgerx.security.jwt.private-key/public-key); "
                + "generating an ephemeral key pair. Tokens will not validate across restarts or "
                + "multiple instances.");
        return generateKeyPair();
    }

    private KeyPair loadConfiguredKeyPair(JwtProperties properties) {
        try {
            var keyFactory = KeyFactory.getInstance(ALGORITHM);
            var privateKeyBytes = Base64.getDecoder().decode(properties.getPrivateKey());
            var publicKeyBytes = Base64.getDecoder().decode(properties.getPublicKey());

            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            return new KeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            throw new IllegalStateException("Invalid Ed25519 JWT signing key configuration", e);
        }
    }

    private KeyPair generateKeyPair() {
        try {
            return KeyPairGenerator.getInstance(ALGORITHM).generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Ed25519 is not available in this JVM", e);
        }
    }
}
