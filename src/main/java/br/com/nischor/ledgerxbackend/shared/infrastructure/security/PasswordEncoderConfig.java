package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderConfig.class);

    private static final String ARGON2ID_ID = "argon2id";
    private static final String PBKDF2_ID = "pbkdf2";

    @Bean
    public PasswordEncoder passwordEncoder() {
        var pbkdf2 = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PBKDF2_ID, pbkdf2);

        String defaultEncoderId = PBKDF2_ID;
        try {
            var argon2id = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            argon2id.encode("argon2id-availability-check");
            encoders.put(ARGON2ID_ID, argon2id);
            defaultEncoderId = ARGON2ID_ID;
        } catch (RuntimeException | LinkageError e) {
            log.warn("Argon2id is not available in this environment, falling back to PBKDF2 for password hashing",
                    e);
        }

        return new DelegatingPasswordEncoder(defaultEncoderId, encoders);
    }
}
