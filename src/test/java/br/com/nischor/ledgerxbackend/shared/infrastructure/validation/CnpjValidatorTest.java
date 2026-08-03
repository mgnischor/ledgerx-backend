package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CnpjValidatorTest {

    private final CnpjValidator validator = new CnpjValidator();

    @Test
    void acceptsNullValue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsValidUnformattedCnpj() {
        assertThat(validator.isValid("11222333000181", null)).isTrue();
    }

    @Test
    void acceptsValidFormattedCnpj() {
        assertThat(validator.isValid("11.222.333/0001-81", null)).isTrue();
    }

    @Test
    void rejectsCnpjWithWrongCheckDigits() {
        assertThat(validator.isValid("11222333000182", null)).isFalse();
    }

    @Test
    void rejectsCnpjWithAllRepeatedDigits() {
        assertThat(validator.isValid("11111111111111", null)).isFalse();
    }

    @Test
    void rejectsCnpjWithWrongLength() {
        assertThat(validator.isValid("1122233300018", null)).isFalse();
    }
}
