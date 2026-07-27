package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Registers {@link GrafanaProperties} as a bean so {@link DeveloperInfoService} can be injected
 * with the configured location of the Grafana instance it queries for version information.
 */
@Configuration
@EnableConfigurationProperties(GrafanaProperties.class)
public class DeveloperDiagnosticsConfig {
}
