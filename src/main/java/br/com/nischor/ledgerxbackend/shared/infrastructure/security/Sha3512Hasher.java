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

    /**
     * Hashes the given string using SHA3-512 after encoding it as UTF-8.
     *
     * @param value the string value to hash
     * @return the lower-case hexadecimal representation of the SHA3-512 digest
     * @throws IllegalStateException if the SHA3-512 algorithm is not available on this JVM
     */
    public String hash(String value) {
        return hash(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Hashes the given raw bytes using SHA3-512.
     *
     * @param value the raw bytes to hash
     * @return the lower-case hexadecimal representation of the SHA3-512 digest
     * @throws IllegalStateException if the SHA3-512 algorithm is not available on this JVM
     */
    public String hash(byte[] value) {
        try {
            var digest = MessageDigest.getInstance(ALGORITHM).digest(value);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("%s algorithm is not available on this JVM".formatted(ALGORITHM), e);
        }
    }
}
