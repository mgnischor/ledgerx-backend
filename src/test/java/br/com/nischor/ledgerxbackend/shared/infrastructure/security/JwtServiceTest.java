package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("ledgerx-backend-test");
        properties.setExpirationSeconds(3600);
        jwtService = new JwtService(keyPair, properties, new ObjectMapper());
    }

    @Test
    void issuesTokenWithThreeSegments() {
        String token = jwtService.issue("user@example.com", Set.of("ADMIN"));

        assertThat(token.split("\\.", -1)).hasSize(3);
    }

    @Test
    void verifyRoundTripsSubjectAndRoles() {
        String token = jwtService.issue("user@example.com", Set.of("ADMIN", "FINANCE"));

        JwtClaims claims = jwtService.verify(token);

        assertThat(claims.subject()).isEqualTo("user@example.com");
        assertThat(claims.roles()).containsExactlyInAnyOrder("ADMIN", "FINANCE");
    }

    @Test
    void rejectsTokenWithTamperedPayload() {
        String token = jwtService.issue("user@example.com", Set.of("VIEWER"));
        String[] segments = token.split("\\.", -1);
        String tampered = segments[0] + "." + segments[1] + "AA" + "." + segments[2];

        assertThatThrownBy(() -> jwtService.verify(tampered)).isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void rejectsTokenSignedByADifferentKeyPair() throws NoSuchAlgorithmException {
        String token = jwtService.issue("user@example.com", Set.of("VIEWER"));

        KeyPair otherKeyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("ledgerx-backend-test");
        JwtService otherJwtService = new JwtService(otherKeyPair, properties, new ObjectMapper());

        assertThatThrownBy(() -> otherJwtService.verify(token)).isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void rejectsExpiredToken() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        JwtProperties expiredProperties = new JwtProperties();
        expiredProperties.setIssuer("ledgerx-backend-test");
        expiredProperties.setExpirationSeconds(-1);
        JwtService expiringJwtService = new JwtService(keyPair, expiredProperties, new ObjectMapper());
        String token = expiringJwtService.issue("user@example.com", Set.of("VIEWER"));

        assertThatThrownBy(() -> expiringJwtService.verify(token)).isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void rejectsMalformedToken() {
        assertThatThrownBy(() -> jwtService.verify("not-a-jwt")).isInstanceOf(InvalidJwtException.class);
    }
}
