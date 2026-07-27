package br.com.nischor.ledgerxbackend.shared.infrastructure.developer;

import java.time.Instant;
import java.util.List;

/**
 * Identifies the running application instance itself.
 *
 * @param name           the Spring application name
 * @param activeProfiles the currently active Spring profiles
 * @param serverTime     the server's current time at the moment the response was built
 */
public record ApplicationInfoDto(String name, List<String> activeProfiles, Instant serverTime) {
}
