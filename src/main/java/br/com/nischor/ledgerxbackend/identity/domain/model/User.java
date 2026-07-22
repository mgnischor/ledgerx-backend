package br.com.nischor.ledgerxbackend.identity.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class User {

    private final UUID id;
    private String fullName;
    private EmailAddress email;
    private String hashedPassword;
    private final Set<Role> roles;
    private boolean active;

    public User(UUID id, String fullName, EmailAddress email, String hashedPassword) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.roles = EnumSet.noneOf(Role.class);
        this.active = true;
    }

    public void grant(Role role) {
        roles.add(role);
    }

    public void revoke(Role role) {
        roles.remove(role);
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }

    public boolean isActive() {
        return active;
    }
}
