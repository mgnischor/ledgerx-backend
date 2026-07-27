package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Gathers the live diagnostics exposed by {@link DeveloperController}: host/container operating
 * system and hardware, JVM details, and the actual versions of RabbitMQ, PostgreSQL and Grafana
 * resolved from those services themselves rather than from static configuration.
 *
 * <p>Every external lookup (database, broker, Grafana) is isolated in its own try/catch so a
 * single unreachable dependency degrades that one field to an {@code "unavailable: ..."} message
 * instead of failing the whole endpoint.
 */
@Service
public class DeveloperInfoService {

    private final Environment environment;
    private final DataSource dataSource;
    private final ConnectionFactory rabbitConnectionFactory;
    private final GrafanaProperties grafanaProperties;
    private final JsonMapper jsonMapper;
    private final HttpClient httpClient;

    /**
     * Creates a new service backed by the given collaborators.
     *
     * @param environment             used to read the application name and active profiles
     * @param dataSource              used to resolve the live PostgreSQL server version
     * @param rabbitConnectionFactory used to open a connection and read the RabbitMQ server
     *                                properties
     * @param grafanaProperties       the configured location of the Grafana instance to query
     * @param jsonMapper              used to parse Grafana's {@code /api/health} JSON response
     */
    public DeveloperInfoService(Environment environment, DataSource dataSource,
            ConnectionFactory rabbitConnectionFactory, GrafanaProperties grafanaProperties, JsonMapper jsonMapper) {
        this.environment = environment;
        this.dataSource = dataSource;
        this.rabbitConnectionFactory = rabbitConnectionFactory;
        this.grafanaProperties = grafanaProperties;
        this.jsonMapper = jsonMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }

    /**
     * Collects a full snapshot of the current runtime environment.
     *
     * @return the assembled {@link DeveloperInfoDto}
     */
    public DeveloperInfoDto collect() {
        return new DeveloperInfoDto(buildApplicationInfo(), buildOperatingSystemInfo(), buildCpuInfo(),
                buildMemoryInfo(), buildStorageInfo(), buildServiceVersions(), buildJavaRuntimeInfo());
    }

    /**
     * Reads the Spring application name, active profiles and current server time.
     *
     * @return the application identity information
     */
    private ApplicationInfoDto buildApplicationInfo() {
        return new ApplicationInfoDto(environment.getProperty("spring.application.name", "ledgerx-backend"),
                Arrays.asList(environment.getActiveProfiles()), Instant.now());
    }

    /**
     * Reads the operating system name/version/architecture and, best-effort, the container/host
     * name.
     *
     * @return the operating system information
     */
    private OperatingSystemInfoDto buildOperatingSystemInfo() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostName = "unknown";
        }
        return new OperatingSystemInfoDto(System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("os.arch"), hostName);
    }

    /**
     * Reads CPU architecture, available processors and load averages, using the HotSpot-specific
     * {@link com.sun.management.OperatingSystemMXBean} for system/process CPU load percentages
     * when available.
     *
     * @return the CPU information
     */
    private CpuInfoDto buildCpuInfo() {
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Double systemCpuLoad = null;
        Double processCpuLoad = null;
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            systemCpuLoad = toPercentage(sunOsBean.getCpuLoad());
            processCpuLoad = toPercentage(sunOsBean.getProcessCpuLoad());
        }
        return new CpuInfoDto(System.getProperty("os.arch"), osBean.getAvailableProcessors(),
                osBean.getSystemLoadAverage(), systemCpuLoad, processCpuLoad);
    }

    /**
     * Converts a HotSpot CPU load ratio (0.0-1.0, or negative when not yet available) to a
     * 0-100 percentage.
     *
     * @param ratio the raw ratio reported by {@link com.sun.management.OperatingSystemMXBean}
     * @return the percentage, or {@code null} if {@code ratio} is negative
     */
    private Double toPercentage(double ratio) {
        return ratio < 0 ? null : ratio * 100;
    }

    /**
     * Reads JVM heap usage and, on HotSpot, total/free physical memory of the underlying
     * container/host.
     *
     * @return the memory information
     */
    private MemoryInfoDto buildMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long jvmUsed = runtime.totalMemory() - runtime.freeMemory();
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        long systemTotal = -1;
        long systemFree = -1;
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            systemTotal = sunOsBean.getTotalMemorySize();
            systemFree = sunOsBean.getFreeMemorySize();
        }
        long systemUsed = (systemTotal >= 0 && systemFree >= 0) ? systemTotal - systemFree : -1;
        return new MemoryInfoDto(jvmUsed, runtime.maxMemory(), runtime.freeMemory(), systemTotal, systemFree,
                systemUsed);
    }

    /**
     * Reads total/usable disk space of the filesystem backing the application's working
     * directory, i.e. the container's writable layer.
     *
     * @return the storage information
     */
    private StorageInfoDto buildStorageInfo() {
        Path path = Path.of(System.getProperty("user.dir"));
        try {
            FileStore fileStore = Files.getFileStore(path);
            long total = fileStore.getTotalSpace();
            long usable = fileStore.getUsableSpace();
            return new StorageInfoDto(path.toString(), total, usable, total - usable);
        } catch (Exception e) {
            return new StorageInfoDto(path.toString(), -1, -1, -1);
        }
    }

    /**
     * Reads JVM vendor/version/name and process identity from the platform's {@link RuntimeMXBean}
     * and {@link ProcessHandle}.
     *
     * @return the Java runtime information
     */
    private JavaRuntimeInfoDto buildJavaRuntimeInfo() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        return new JavaRuntimeInfoDto(System.getProperty("java.vendor"), System.getProperty("java.version"),
                runtimeMxBean.getVmName(), runtimeMxBean.getVmVersion(), System.getProperty("java.home"),
                ProcessHandle.current().pid(), Instant.ofEpochMilli(runtimeMxBean.getStartTime()),
                runtimeMxBean.getUptime());
    }

    /**
     * Resolves the live versions of RabbitMQ, PostgreSQL and Grafana. Each lookup fails
     * independently: an unreachable dependency yields an {@code "unavailable: ..."} value for that
     * field rather than aborting the whole diagnostics response.
     *
     * @return the dependent-service versions
     */
    private ServiceVersionsDto buildServiceVersions() {
        return new ServiceVersionsDto(resolveRabbitMqVersion(), resolvePostgreSqlVersion(), resolveGrafanaVersion());
    }

    /**
     * Opens a connection to the configured RabbitMQ broker and reads its {@code version} server
     * property.
     *
     * @return the RabbitMQ server version, or an {@code "unavailable: ..."} message if the broker
     *         could not be reached
     */
    private String resolveRabbitMqVersion() {
        try (org.springframework.amqp.rabbit.connection.Connection connection =
                rabbitConnectionFactory.createConnection()) {
            Object version = connection.getDelegate().getServerProperties().get("version");
            return version == null ? "unavailable: version not reported by broker" : String.valueOf(version);
        } catch (Exception e) {
            return "unavailable: " + e.getMessage();
        }
    }

    /**
     * Opens a JDBC connection to the configured PostgreSQL database and reads its product version
     * from the connection metadata.
     *
     * @return the PostgreSQL server version, or an {@code "unavailable: ..."} message if the
     *         database could not be reached
     */
    private String resolvePostgreSqlVersion() {
        try (java.sql.Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getDatabaseProductVersion();
        } catch (SQLException e) {
            return "unavailable: " + e.getMessage();
        }
    }

    /**
     * Calls the configured Grafana instance's {@code /api/health} endpoint and reads its
     * {@code version} field.
     *
     * @return the Grafana version, or an {@code "unavailable: ..."} message if Grafana could not
     *         be reached
     */
    private String resolveGrafanaVersion() {
        try {
            URI uri = URI.create("%s://%s:%d/api/health".formatted(grafanaProperties.getScheme(),
                    grafanaProperties.getHost(), grafanaProperties.getPort()));
            HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(2)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "unavailable: HTTP " + response.statusCode();
            }
            JsonNode body = jsonMapper.readTree(response.body());
            JsonNode version = body.get("version");
            return version == null ? "unavailable: version not reported" : version.asString();
        } catch (Exception e) {
            return "unavailable: " + e.getMessage();
        }
    }
}
