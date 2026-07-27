package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates requests bearing a valid {@code Authorization: Bearer <token>} header, populating
 * the {@link SecurityContextHolder} so downstream authorization checks can rely on it. Requests
 * without a bearer token, or with an invalid one, are passed through unauthenticated so that
 * {@link SecurityConfig}'s access rules (not this filter) decide whether the endpoint requires
 * authentication.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    /**
     * Creates a new filter backed by the given JWT service.
     *
     * @param jwtService the service used to verify bearer tokens
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Inspects the {@code Authorization} header of the incoming request: if it carries a
     * {@code Bearer} token, attempts to verify it and populate the {@link SecurityContextHolder}
     * with the resulting authentication. Invalid tokens are logged at debug level and the security
     * context is cleared, but the request is still passed down the filter chain unauthenticated.
     * Requests without a bearer token are passed through unchanged.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the remaining filter chain to invoke
     * @throws ServletException if an error occurs while processing the filter chain
     * @throws IOException      if an I/O error occurs while processing the filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            try {
                authenticate(token);
            } catch (InvalidJwtException e) {
                log.debug("Rejected invalid JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifies the given JWT and populates the security context with an authenticated token
     * whose granted authorities are derived from the JWT's roles (prefixed with {@code ROLE_})
     * and permissions (prefixed with {@code PERMISSION_}).
     *
     * @param token the raw (unprefixed) JWT string extracted from the Authorization header
     * @throws InvalidJwtException if the token is malformed, has an invalid signature, or has expired
     */
    private void authenticate(String token) {
        JwtClaims claims = jwtService.verify(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        claims.roles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        claims.permissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission)));

        var authentication = new UsernamePasswordAuthenticationToken(claims.subject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
