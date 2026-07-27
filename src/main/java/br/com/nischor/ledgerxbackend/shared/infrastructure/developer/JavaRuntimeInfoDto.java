package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import java.time.Instant;

/**
 * Identifies the Java runtime executing the application and where it is installed.
 *
 * @param vendor      the JVM vendor, e.g. {@code Eclipse Adoptium}
 * @param version     the {@code java.version} of the running JVM
 * @param vmName      the JVM implementation name, e.g. {@code OpenJDK 64-Bit Server VM}
 * @param vmVersion   the JVM implementation version
 * @param javaHome    the filesystem path the JVM was launched from ({@code java.home})
 * @param pid         the operating-system process id of this JVM
 * @param startTime   the instant the JVM started
 * @param uptimeMillis how long the JVM has been running, in milliseconds
 */
public record JavaRuntimeInfoDto(String vendor, String version, String vmName, String vmVersion, String javaHome,
        long pid, Instant startTime, long uptimeMillis) {
}
