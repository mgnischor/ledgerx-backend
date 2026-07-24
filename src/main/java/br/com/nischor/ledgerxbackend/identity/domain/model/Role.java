package br.com.nischor.ledgerxbackend.identity.domain.model;

/**
 * Authorization profiles a {@link User} can be granted. See {@code RolePermissions} for the
 * concrete set of {@code Permission}s each role carries.
 */
public enum Role {
    /** Full access, plus debug-mode tooling not available to any other role. */
    DEVELOPER,
    /** Full access to business operations (no debug tooling). */
    ADMINISTRATOR,
    /** Can create and change records, and approve changes made by collaborators. */
    MANAGER,
    /** Can create and change records, but cannot approve or delete them. */
    COLLABORATOR
}
