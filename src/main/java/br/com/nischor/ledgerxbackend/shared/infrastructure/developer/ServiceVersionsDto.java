package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

/**
 * Versions of the external services the application depends on, resolved live from each service
 * rather than read from configuration. Any field holds {@code "unavailable: <reason>"} instead of
 * throwing if the corresponding service could not be reached.
 *
 * @param rabbitMq   the RabbitMQ broker version, as reported by its server properties
 * @param postgreSql the PostgreSQL server version, as reported by the JDBC connection metadata
 * @param grafana    the Grafana version, as reported by its {@code /api/health} endpoint
 */
public record ServiceVersionsDto(String rabbitMq, String postgreSql, String grafana) {
}
