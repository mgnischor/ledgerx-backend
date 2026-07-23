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
    private final ObjectMapper objectMapper;

    public JwtService(KeyPair keyPair, JwtProperties properties, ObjectMapper objectMapper) {
        this.keyPair = keyPair;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String issue(String subject, Set<String> roles) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(properties.getExpirationSeconds());

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", JWA_ALGORITHM);
        header.put("typ", TOKEN_TYPE);

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("iss", properties.getIssuer());
        claims.put("sub", subject);
        claims.put("roles", roles);
        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());

        String signingInput = encodeSegment(header) + "." + encodeSegment(claims);
        String signature = URL_ENCODER.encodeToString(sign(signingInput));
        return signingInput + "." + signature;
    }

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
        return new JwtClaims(subject, roles);
    }

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

    private String encodeSegment(Map<String, Object> segment) {
        try {
            return URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(segment));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode JWT segment", e);
        }
    }

    private Map<String, Object> decodeSegment(String segment) {
        try {
            return objectMapper.readValue(URL_DECODER.decode(segment), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new InvalidJwtException("Failed to decode JWT segment", e);
        }
    }
}
