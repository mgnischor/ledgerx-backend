package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

/**
 * Memory usage of both the JVM heap and the underlying container/host.
 *
 * @param jvmUsedBytes    bytes currently used on the JVM heap ({@code totalMemory - freeMemory})
 * @param jvmMaxBytes     the maximum number of bytes the JVM heap can grow to
 * @param jvmFreeBytes    bytes currently unused within the JVM's already-allocated heap
 * @param systemTotalBytes total physical memory visible to the OS/container, or {@code -1} if
 *                         unavailable on this platform
 * @param systemFreeBytes  free physical memory available to the OS/container, or {@code -1} if
 *                         unavailable on this platform
 * @param systemUsedBytes  used physical memory ({@code systemTotalBytes - systemFreeBytes}), or
 *                         {@code -1} if unavailable on this platform
 */
public record MemoryInfoDto(long jvmUsedBytes, long jvmMaxBytes, long jvmFreeBytes, long systemTotalBytes,
        long systemFreeBytes, long systemUsedBytes) {
}
