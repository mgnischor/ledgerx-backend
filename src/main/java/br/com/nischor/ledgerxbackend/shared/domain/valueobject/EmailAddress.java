package br.com.nischor.ledgerxbackend.shared.domain.valueobject;

import java.io.Serializable;
import java.util.regex.Pattern;

public record EmailAddress(String value) implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public EmailAddress {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email address: %s".formatted(value));
        }
        value = value.toLowerCase();
    }
}
