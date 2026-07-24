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

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

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

    private void authenticate(String token) {
        JwtClaims claims = jwtService.verify(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        claims.roles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        claims.permissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission)));

        var authentication = new UsernamePasswordAuthenticationToken(claims.subject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
