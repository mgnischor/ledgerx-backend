package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

/**
 * Identifies the operating system and host the JVM is currently running on.
 *
 * @param name         the operating system name, e.g. {@code Linux}
 * @param version      the operating system version/kernel release string
 * @param architecture the CPU architecture the OS reports, e.g. {@code amd64} or {@code aarch64}
 * @param hostName     the container/host name, or {@code "unknown"} if it could not be resolved
 */
public record OperatingSystemInfoDto(String name, String version, String architecture, String hostName) {
}
