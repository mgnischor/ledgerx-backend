package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

/**
 * SpEL expression constants for {@code @PreAuthorize}, matching the {@code PERMISSION_*}/
 * {@code ROLE_*} authorities granted from {@code Role}/{@code RolePermissions} by
 * {@code JwtAuthenticationFilter} and {@code LedgerxUserDetailsService}. Java annotations require
 * compile-time constants, so these cannot be enum-backed.
 */
public final class Authorizations {

    public static final String READ = "hasAuthority('PERMISSION_READ')";
    public static final String CREATE = "hasAuthority('PERMISSION_CREATE')";
    public static final String UPDATE = "hasAuthority('PERMISSION_UPDATE')";
    public static final String DELETE = "hasAuthority('PERMISSION_DELETE')";
    public static final String APPROVE = "hasAuthority('PERMISSION_APPROVE')";
    public static final String DEBUG = "hasAuthority('PERMISSION_DEBUG')";

    /** User-management actions (granting roles, deactivating accounts): full-access roles only. */
    public static final String FULL_ACCESS = "hasAnyRole('DEVELOPER','ADMINISTRATOR')";

    private Authorizations() {
    }
}
