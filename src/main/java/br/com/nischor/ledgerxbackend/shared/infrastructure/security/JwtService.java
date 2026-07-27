package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Issues and verifies compact JWS tokens signed with Ed25519 (JWA algorithm name "EdDSA"), built
 * directly on {@code java.security} rather than a third-party JWT library, since the JDK has had
 * native Ed25519 {@link java.security.Signature} support since Java 15.
 *
 * <p>Uses its own private {@link ObjectMapper} instance rather than injecting Spring's
 * autoconfigured bean: this app's classpath carries both Jackson 2 and Jackson 3 (pulled in
 * transitively by Spring Security's OAuth2/JOSE modules), and which {@code ObjectMapper} type
 * Boot's {@code JacksonAutoConfiguration} exposes as a bean is an implementation detail that has
 * flipped between them across Boot versions. Claims here are a flat map of strings/numbers/
 * collections, so no Spring-managed modules (Java Time, etc.) are needed anyway.
 */
@Service
public class JwtService {

    private static final String JWA_ALGORITHM = "EdDSA";
    private static final String JCA_ALGORITHM = "Ed25519";
    private static final String TOKEN_TYPE = "JWT";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final KeyPair keyPair;
    private final JwtProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a new JWT service using the given signing key pair and configuration.
     *
     * @param keyPair    the Ed25519 key pair used for signing (private key) and verifying (public key) tokens
     * @param properties the JWT configuration (issuer, expiration)
     */
    public JwtService(KeyPair keyPair, JwtProperties properties) {
        this.keyPair = keyPair;
        this.properties = properties;
    }

    /**
     * Issues a new compact JWS token signed with Ed25519 (EdDSA), embedding the given subject,
     * roles and permissions along with the configured issuer and an expiration computed from
     * {@link JwtProperties#getExpirationSeconds()}.
     *
     * @param subject     the subject (typically the user identifier) to embed in the {@code sub} claim
     * @param roles       the roles to embed in the {@code roles} claim
     * @param permissions the permissions to embed in the {@code permissions} claim
     * @return the compact, Base64url-encoded, dot-separated JWS token
     */
    public String issue(String subject, Set<String> roles, Set<String> permissions) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(properties.getExpirationSeconds());

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", JWA_ALGORITHM);
        header.put("typ", TOKEN_TYPE);

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("iss", properties.getIssuer());
        claims.put("sub", subject);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());

        String signingInput = encodeSegment(header) + "." + encodeSegment(claims);
        String signature = URL_ENCODER.encodeToString(sign(signingInput));
        return signingInput + "." + signature;
    }

    /**
     * Verifies and decodes a compact JWS token: checks that it has exactly three dot-separated
     * segments, verifies the Ed25519 signature over the header/payload, checks that it has not
     * expired, and checks that the {@code iss} claim matches the configured issuer.
     *
     * @param token the compact JWS token to verify
     * @return the decoded {@link JwtClaims} extracted from the token payload
     * @throws InvalidJwtException if the token is malformed, has an invalid signature,
     *                              has expired, or was issued by an unexpected issuer
     */
    public JwtClaims verify(String token) {
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3) {
            throw new InvalidJwtException("Malformed JWT: expected 3 segments, got %d".formatted(parts.length));
        }

        String signingInput = parts[0] + "." + parts[1];
        if (!isSignatureValid(signingInput, URL_DECODER.decode(parts[2]))) {
            throw new InvalidJwtException("JWT signature verification failed");
        }

        Map<String, Object> claims = decodeSegment(parts[1]);

        long expiresAtEpochSecond = ((Number) claims.getOrDefault("exp", 0)).longValue();
        if (Instant.now().isAfter(Instant.ofEpochSecond(expiresAtEpochSecond))) {
            throw new InvalidJwtException("JWT has expired");
        }

        String issuer = (String) claims.get("iss");
        if (!properties.getIssuer().equals(issuer)) {
            throw new InvalidJwtException("Unexpected JWT issuer: %s".formatted(issuer));
        }

        String subject = (String) claims.get("sub");
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getOrDefault("roles", List.of());
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) claims.getOrDefault("permissions", List.of());
        return new JwtClaims(subject, roles, permissions);
    }

    /**
     * Signs the given signing input (header and payload, dot-separated) using the Ed25519
     * private key.
     *
     * @param signingInput the ASCII signing input to sign
     * @return the raw Ed25519 signature bytes
     * @throws IllegalStateException if signing fails due to a security provider error
     */
    private byte[] sign(String signingInput) {
        try {
            Signature signature = Signature.getInstance(JCA_ALGORITHM);
            signature.initSign(keyPair.getPrivate());
            signature.update(signingInput.getBytes(StandardCharsets.US_ASCII));
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to sign JWT with Ed25519", e);
        }
    }

    /**
     * Verifies the given Ed25519 signature against the signing input using the public key.
     *
     * @param signingInput   the ASCII signing input (header and payload, dot-separated)
     * @param signatureBytes the raw signature bytes to verify
     * @return {@code true} if the signature is valid for the given input, {@code false} otherwise
     * @throws InvalidJwtException if verification fails due to a security provider error
     */
    private boolean isSignatureValid(String signingInput, byte[] signatureBytes) {
        try {
            Signature signature = Signature.getInstance(JCA_ALGORITHM);
            signature.initVerify(keyPair.getPublic());
            signature.update(signingInput.getBytes(StandardCharsets.US_ASCII));
            return signature.verify(signatureBytes);
        } catch (GeneralSecurityException e) {
            throw new InvalidJwtException("Failed to verify JWT signature", e);
        }
    }

    /**
     * Serializes the given claims map to JSON and Base64url-encodes it (without padding).
     *
     * @param segment the header or payload map to encode
     * @return the Base64url-encoded JSON representation of {@code segment}
     * @throws IllegalStateException if the segment cannot be serialized
     */
    private String encodeSegment(Map<String, Object> segment) {
        try {
            return URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(segment));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode JWT segment", e);
        }
    }

    /**
     * Base64url-decodes the given segment and parses it as a JSON claims map.
     *
     * @param segment the Base64url-encoded JSON segment to decode
     * @return the decoded claims map
     * @throws InvalidJwtException if the segment cannot be decoded or parsed as JSON
     */
    private Map<String, Object> decodeSegment(String segment) {
        try {
            return objectMapper.readValue(URL_DECODER.decode(segment), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new InvalidJwtException("Failed to decode JWT segment", e);
        }
    }
}
