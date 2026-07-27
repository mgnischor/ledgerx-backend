package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

/**
 * SpEL expression constants for {@code @PreAuthorize}, matching the {@code PERMISSION_*}/
 * {@code ROLE_*} authorities granted from {@code Role}/{@code RolePermissions} by
 * {@code JwtAuthenticationFilter} and {@code LedgerxUserDetailsService}. Java annotations require
 * compile-time constants, so these cannot be enum-backed.
 */
public final class Authorizations {

    /** SpEL expression requiring the {@code PERMISSION_READ} authority. */
    public static final String READ = "hasAuthority('PERMISSION_READ')";
    /** SpEL expression requiring the {@code PERMISSION_CREATE} authority. */
    public static final String CREATE = "hasAuthority('PERMISSION_CREATE')";
    /** SpEL expression requiring the {@code PERMISSION_UPDATE} authority. */
    public static final String UPDATE = "hasAuthority('PERMISSION_UPDATE')";
    /** SpEL expression requiring the {@code PERMISSION_DELETE} authority. */
    public static final String DELETE = "hasAuthority('PERMISSION_DELETE')";
    /** SpEL expression requiring the {@code PERMISSION_APPROVE} authority. */
    public static final String APPROVE = "hasAuthority('PERMISSION_APPROVE')";
    /** SpEL expression requiring the {@code PERMISSION_DEBUG} authority. */
    public static final String DEBUG = "hasAuthority('PERMISSION_DEBUG')";

    /** User-management actions (granting roles, deactivating accounts): full-access roles only. */
    public static final String FULL_ACCESS = "hasAnyRole('DEVELOPER','ADMINISTRATOR')";

    /**
     * Prevents instantiation; this class only exposes static constants.
     */
    private Authorizations() {
    }
}
