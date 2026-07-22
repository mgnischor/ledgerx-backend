package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

/**
 * Standard non-password hashing utility for the project (checksums, fingerprints, idempotency
 * keys, etc). Password hashing must go through {@link PasswordEncoderConfig} instead.
 */
@Component
public class Sha3512Hasher {

    private static final String ALGORITHM = "SHA3-512";

    public String hash(String value) {
        return hash(value.getBytes(StandardCharsets.UTF_8));
    }

    public String hash(byte[] value) {
        try {
            var digest = MessageDigest.getInstance(ALGORITHM).digest(value);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("%s algorithm is not available on this JVM".formatted(ALGORITHM), e);
        }
    }
}
