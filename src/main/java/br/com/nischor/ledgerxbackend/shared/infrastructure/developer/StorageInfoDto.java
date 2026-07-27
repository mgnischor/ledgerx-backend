package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

/**
 * Disk usage of the filesystem root, i.e. the container's writable layer.
 *
 * @param path        the filesystem path this information was measured against
 * @param totalBytes  total capacity of the filesystem, or {@code -1} if it could not be read
 * @param usableBytes bytes available to the JVM process, or {@code -1} if it could not be read
 * @param usedBytes   bytes already used ({@code totalBytes - usableBytes}), or {@code -1} if it
 *                    could not be computed
 */
public record StorageInfoDto(String path, long totalBytes, long usableBytes, long usedBytes) {
}
