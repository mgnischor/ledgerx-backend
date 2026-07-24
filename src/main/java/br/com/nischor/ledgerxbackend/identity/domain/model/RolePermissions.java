package br.com.nischor.ledgerxbackend.identity.domain.model;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Single source of truth for which {@link Permission}s each {@link Role} carries:
 *
 * <ul>
 * <li>{@link Role#DEVELOPER} — full access plus debug-mode tooling.</li>
 * <li>{@link Role#ADMINISTRATOR} — full access (no debug tooling).</li>
 * <li>{@link Role#MANAGER} — add, change and approve changes (no delete).</li>
 * <li>{@link Role#COLLABORATOR} — add and change only.</li>
 * </ul>
 */
public final class RolePermissions {

    private static final Map<Role, Set<Permission>> PERMISSIONS_BY_ROLE = new EnumMap<>(Role.class);

    static {
        PERMISSIONS_BY_ROLE.put(Role.DEVELOPER, EnumSet.allOf(Permission.class));
        PERMISSIONS_BY_ROLE.put(Role.ADMINISTRATOR,
                EnumSet.of(Permission.READ, Permission.CREATE, Permission.UPDATE, Permission.DELETE,
                        Permission.APPROVE));
        PERMISSIONS_BY_ROLE.put(Role.MANAGER,
                EnumSet.of(Permission.READ, Permission.CREATE, Permission.UPDATE, Permission.APPROVE));
        PERMISSIONS_BY_ROLE.put(Role.COLLABORATOR, EnumSet.of(Permission.READ, Permission.CREATE, Permission.UPDATE));
    }

    private RolePermissions() {
    }

    public static Set<Permission> of(Role role) {
        return PERMISSIONS_BY_ROLE.get(role);
    }

    public static Set<Permission> of(Set<Role> roles) {
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);
        roles.forEach(role -> permissions.addAll(of(role)));
        return permissions;
    }
}
