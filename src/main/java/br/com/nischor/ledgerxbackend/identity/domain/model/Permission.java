package br.com.nischor.ledgerxbackend.identity.domain.model;

/**
 * Fine-grained action a {@link Role} may be allowed to perform, enforced via
 * {@code @PreAuthorize("hasAuthority('PERMISSION_" + name() + "')")} on use cases/endpoints. See
 * {@link RolePermissions} for the role-to-permission mapping.
 */
public enum Permission {
    /** View existing records. */
    READ,
    /** Add new records ("Adicionar"). */
    CREATE,
    /** Modify existing records ("Alterar"). */
    UPDATE,
    /** Remove records. Only full-access roles (Developer, Administrator). */
    DELETE,
    /** Approve changes made by another user ("Aprovar alterações"). */
    APPROVE,
    /** Access debug-mode tooling and diagnostics. Developer only. */
    DEBUG
}
