package br.com.nischor.ledgerxbackend.identity.infrastructure.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the DEVELOPER account created on first startup by {@link AdminBootstrapRunner}
 * so the API is never in a state where no one can grant roles or manage other accounts. Override
 * every field in production; the defaults are meant for local development only.
 */
@ConfigurationProperties(prefix = "ledgerx.security.bootstrap-admin")
public class AdminBootstrapProperties {

    private boolean enabled = true;
    private String fullName = "System Administrator";
    private String email = "admin@ledgerx.local";
    private String password = "ChangeMe@2026";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
