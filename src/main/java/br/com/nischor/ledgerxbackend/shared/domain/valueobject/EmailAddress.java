package br.com.nischor.ledgerxbackend.shared.domain.valueobject;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Immutable value object representing a validated, normalized e-mail address.
 *
 * <p>The value is validated against a basic e-mail pattern and normalized to
 * lower case upon construction.
 *
 * @param value the e-mail address, normalized to lower case
 */
public record EmailAddress(String value) implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");

    /**
     * Validates and normalizes the e-mail address.
     *
     * @throws IllegalArgumentException if {@code value} is {@code null} or does not match a valid e-mail format
     */
    public EmailAddress {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email address: %s".formatted(value));
        }
        value = value.toLowerCase();
    }
}
