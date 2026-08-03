package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BrazilianStateValidatorTest {

    private final BrazilianStateValidator validator = new BrazilianStateValidator();

    @Test
    void acceptsNullValue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsKnownStateCode() {
        assertThat(validator.isValid("SP", null)).isTrue();
    }

    @Test
    void acceptsKnownStateCodeRegardlessOfCase() {
        assertThat(validator.isValid("sp", null)).isTrue();
    }

    @Test
    void rejectsUnknownStateCode() {
        assertThat(validator.isValid("XX", null)).isFalse();
    }
}
