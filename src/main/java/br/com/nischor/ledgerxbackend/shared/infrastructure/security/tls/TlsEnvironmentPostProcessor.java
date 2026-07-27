package br.com.nischor.ledgerxbackend.shared.infrastructure.security.tls;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Bootstraps HTTPS for the embedded server by generating a fresh self-signed certificate on every
 * startup (see {@link SelfSignedCertificateGenerator}) and injecting the resulting keystore as
 * {@code server.ssl.*} properties, restricted to TLS 1.3 with a TLS 1.2 fallback. This runs as a
 * Spring Boot {@link EnvironmentPostProcessor} (registered in {@code META-INF/spring.factories})
 * so the keystore exists before the embedded web server factory reads {@code server.ssl.*}.
 *
 * <p>Set {@code ledgerx.security.tls.enabled=false} to skip this (e.g. behind a TLS-terminating
 * reverse proxy), or set {@code server.ssl.key-store} explicitly to supply a real certificate
 * instead of the generated one.
 */
public class TlsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(TlsEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "ledgerxSelfSignedTls";
    private static final String CERTIFICATE_ALIAS = "ledgerx";
    private static final Duration CERTIFICATE_VALIDITY = Duration.ofDays(365);

    /**
     * Generates a self-signed TLS certificate and injects it as {@code server.ssl.*} properties,
     * unless TLS bootstrapping is disabled ({@code ledgerx.security.tls.enabled=false}) or an
     * explicit {@code server.ssl.key-store} is already configured.
     *
     * @param environment the environment to inspect and mutate with the generated SSL properties
     * @param application the running {@link SpringApplication} (unused, required by the interface)
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.getProperty("ledgerx.security.tls.enabled", Boolean.class, true)) {
            return;
        }
        if (environment.getProperty("server.ssl.key-store") != null) {
            return;
        }

        String commonName = environment.getProperty("ledgerx.security.tls.common-name", "localhost");
        char[] keystorePassword = generatePassword();

        SelfSignedCertificateGenerator.GeneratedKeystore keystore = SelfSignedCertificateGenerator
                .generate(commonName, CERTIFICATE_ALIAS, keystorePassword, CERTIFICATE_VALIDITY);
        log.info("Generated ephemeral self-signed TLS certificate for CN={} at {}", commonName, keystore.path());

        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME,
                sslProperties(keystore, keystorePassword)));
    }

    /**
     * Builds the {@code server.ssl.*} property map pointing at the given generated keystore,
     * enabling TLS with protocol {@code TLS} restricted to {@code TLSv1.3,TLSv1.2}.
     *
     * @param keystore         the generated keystore to reference
     * @param keystorePassword the password protecting the keystore
     * @return a map of {@code server.ssl.*} property names to values
     */
    private Map<String, Object> sslProperties(SelfSignedCertificateGenerator.GeneratedKeystore keystore,
            char[] keystorePassword) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("server.ssl.enabled", true);
        properties.put("server.ssl.key-store", "file:" + keystore.path());
        properties.put("server.ssl.key-store-type", "PKCS12");
        properties.put("server.ssl.key-store-password", new String(keystorePassword));
        properties.put("server.ssl.key-alias", keystore.alias());
        properties.put("server.ssl.protocol", "TLS");
        properties.put("server.ssl.enabled-protocols", "TLSv1.3,TLSv1.2");
        return properties;
    }

    /**
     * Generates a random, URL-safe Base64-encoded password from 32 secure random bytes.
     *
     * @return a freshly generated random password
     */
    private char[] generatePassword() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).toCharArray();
    }

    /**
     * Returns the ordering value used by Spring Boot to sequence environment post processors,
     * placed immediately after {@link ConfigDataEnvironmentPostProcessor} so that configuration
     * files have already been loaded (allowing {@code ledgerx.security.tls.*} overrides) before
     * this processor runs.
     *
     * @return {@link ConfigDataEnvironmentPostProcessor#ORDER} plus one
     */
    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }
}
