package br.com.nischor.ledgerxbackend.shared.infrastructure.security.debug;

import java.time.Instant;
import java.util.List;

/**
 * Runtime and build diagnostics returned by {@link DebugController}.
 *
 * @param applicationName the Spring application name
 * @param activeProfiles  the currently active Spring profiles
 * @param javaVersion     the JVM's {@code java.version} system property value
 * @param uptimeMillis    the JVM uptime in milliseconds, as reported by the runtime MX bean
 * @param serverTime      the server's current time at the moment the response was built
 */
public record DebugInfoDto(String applicationName, List<String> activeProfiles, String javaVersion,
        long uptimeMillis, Instant serverTime) {
}
