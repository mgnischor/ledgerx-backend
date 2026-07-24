package br.com.nischor.ledgerxbackend.shared.infrastructure.security.debug;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Debug-mode tooling available only to the {@code DEVELOPER} role (not even Administrator):
 * attaches {@code X-Debug-Request-Id} and {@code X-Debug-Duration-Ms} response headers whenever
 * the authenticated caller holds the {@code PERMISSION_DEBUG} authority, to help trace individual
 * requests without needing an external APM tool. No-op for every other caller.
 */
public class DebugModeFilter extends OncePerRequestFilter {

    private static final String DEBUG_AUTHORITY = "PERMISSION_DEBUG";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!hasDebugPermission()) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestId = UUID.randomUUID().toString();
        response.setHeader("X-Debug-Request-Id", requestId);
        long startNanos = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            if (!response.isCommitted()) {
                response.setHeader("X-Debug-Duration-Ms", String.valueOf(durationMs));
            }
        }
    }

    private boolean hasDebugPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> DEBUG_AUTHORITY.equals(authority.getAuthority()));
    }
}
