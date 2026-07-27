package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Location of the Grafana instance queried by {@link DeveloperInfoService} to report its version.
 * Defaults match the {@code grafana-lgtm} service name and port exposed in the project's
 * {@code compose.yaml}, so no configuration is needed when running via Docker Compose on the same
 * network.
 */
@ConfigurationProperties(prefix = "ledgerx.observability.grafana")
public class GrafanaProperties {

    /** URI scheme used to reach Grafana's HTTP API. */
    private String scheme = "http";
    /** Hostname or IP address of the Grafana instance. */
    private String host = "grafana-lgtm";
    /** Port Grafana's HTTP API listens on. */
    private int port = 3000;

    /**
     * Returns the URI scheme used to reach Grafana's HTTP API.
     *
     * @return the configured scheme, e.g. {@code http} or {@code https}.
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the URI scheme used to reach Grafana's HTTP API.
     *
     * @param scheme the scheme to use.
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Returns the hostname or IP address of the Grafana instance.
     *
     * @return the configured host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the hostname or IP address of the Grafana instance.
     *
     * @param host the host to use.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns the port Grafana's HTTP API listens on.
     *
     * @return the configured port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port Grafana's HTTP API listens on.
     *
     * @param port the port to use.
     */
    public void setPort(int port) {
        this.port = port;
    }
}
