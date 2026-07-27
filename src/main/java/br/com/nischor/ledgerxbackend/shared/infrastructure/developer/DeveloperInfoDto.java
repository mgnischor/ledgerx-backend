package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

/**
 * Full compendium of runtime-environment diagnostics returned by {@link DeveloperController},
 * aggregating operating system, CPU, memory, storage, dependent-service versions, Java runtime and
 * application identity information gathered by {@link DeveloperInfoService}.
 *
 * @param application     identity of the running application instance
 * @param operatingSystem the host/container operating system
 * @param cpu             CPU characteristics and current load
 * @param memory          JVM heap and system memory usage
 * @param storage         filesystem usage of the container's writable layer
 * @param services        versions of the RabbitMQ, PostgreSQL and Grafana dependencies
 * @param javaRuntime     the Java runtime executing the application
 */
public record DeveloperInfoDto(ApplicationInfoDto application, OperatingSystemInfoDto operatingSystem, CpuInfoDto cpu,
        MemoryInfoDto memory, StorageInfoDto storage, ServiceVersionsDto services,
        JavaRuntimeInfoDto javaRuntime) {
}
