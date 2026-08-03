package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CpfValidatorTest {

    private final CpfValidator validator = new CpfValidator();

    @Test
    void acceptsNullValue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsValidUnformattedCpf() {
        assertThat(validator.isValid("11144477735", null)).isTrue();
    }

    @Test
    void acceptsValidFormattedCpf() {
        assertThat(validator.isValid("111.444.777-35", null)).isTrue();
    }

    @Test
    void rejectsCpfWithWrongCheckDigits() {
        assertThat(validator.isValid("11144477736", null)).isFalse();
    }

    @Test
    void rejectsCpfWithAllRepeatedDigits() {
        assertThat(validator.isValid("11111111111", null)).isFalse();
    }

    @Test
    void rejectsCpfWithWrongLength() {
        assertThat(validator.isValid("1114447773", null)).isFalse();
    }
}
