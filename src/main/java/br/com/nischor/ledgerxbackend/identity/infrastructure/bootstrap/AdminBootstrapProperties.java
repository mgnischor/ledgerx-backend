package br.com.nischor.ledgerxbackend.identity.infrastructure.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the DEVELOPER account created on first startup by {@link AdminBootstrapRunner}
 * so the API is never in a state where no one can grant roles or manage other accounts. Override
 * every field in production; the defaults are meant for local development only.
 */
@ConfigurationProperties(prefix = "ledgerx.security.bootstrap-admin")
public class AdminBootstrapProperties {

    /** Whether the bootstrap admin account creation is active. */
    private boolean enabled = true;
    /** Full name assigned to the bootstrap admin account. */
    private String fullName = "System Administrator";
    /** Email address assigned to the bootstrap admin account. */
    private String email = "admin@ledgerx.local";
    /** Plain-text password to be hashed and assigned to the bootstrap admin account. */
    private String password = "ChangeMe@2026";

    /**
     * Returns whether bootstrap admin account creation is enabled.
     *
     * @return {@code true} if enabled, {@code false} otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether bootstrap admin account creation is enabled.
     *
     * @param enabled {@code true} to enable, {@code false} to disable.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the full name to assign to the bootstrap admin account.
     *
     * @return the configured full name.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name to assign to the bootstrap admin account.
     *
     * @param fullName the full name to use.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns the email address to assign to the bootstrap admin account.
     *
     * @return the configured email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address to assign to the bootstrap admin account.
     *
     * @param email the email address to use.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the plain-text password to hash and assign to the bootstrap admin account.
     *
     * @return the configured password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the plain-text password to hash and assign to the bootstrap admin account.
     *
     * @param password the password to use.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
