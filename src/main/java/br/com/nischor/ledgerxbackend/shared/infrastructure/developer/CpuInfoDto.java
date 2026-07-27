package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

/**
 * CPU characteristics and load of the container the JVM is running in.
 *
 * @param architecture               the CPU architecture, e.g. {@code amd64} or {@code aarch64}
 * @param availableProcessors        the number of processors visible to the JVM (respects
 *                                   container CPU quotas/cgroup limits)
 * @param systemLoadAverage          the system load average over the last minute, or {@code -1}
 *                                   if unavailable on this platform
 * @param systemCpuLoadPercentage    the overall system CPU load as a percentage (0-100), or
 *                                   {@code null} if the platform-specific MX bean is unavailable
 * @param processCpuLoadPercentage   this JVM process's own CPU load as a percentage (0-100), or
 *                                   {@code null} if the platform-specific MX bean is unavailable
 */
public record CpuInfoDto(String architecture, int availableProcessors, double systemLoadAverage,
        Double systemCpuLoadPercentage, Double processCpuLoadPercentage) {
}
