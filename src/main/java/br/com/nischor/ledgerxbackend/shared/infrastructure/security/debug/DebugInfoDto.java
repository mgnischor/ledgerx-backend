package br.com.nischor.ledgerxbackend.shared.infrastructure.security.debug;

import java.time.Instant;
import java.util.List;

public record DebugInfoDto(String applicationName, List<String> activeProfiles, String javaVersion,
        long uptimeMillis, Instant serverTime) {
}
