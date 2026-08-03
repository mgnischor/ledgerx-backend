package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StrongPasswordValidatorTest {

    private final StrongPasswordValidator validator = new StrongPasswordValidator();

    @Test
    void acceptsNullValue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsPasswordWithAllRequiredCharacterClasses() {
        assertThat(validator.isValid("Str0ng!Pass", null)).isTrue();
    }

    @Test
    void rejectsPasswordMissingUppercase() {
        assertThat(validator.isValid("str0ng!pass", null)).isFalse();
    }

    @Test
    void rejectsPasswordMissingLowercase() {
        assertThat(validator.isValid("STR0NG!PASS", null)).isFalse();
    }

    @Test
    void rejectsPasswordMissingDigit() {
        assertThat(validator.isValid("Strong!Pass", null)).isFalse();
    }

    @Test
    void rejectsPasswordMissingSpecialCharacter() {
        assertThat(validator.isValid("Str0ngPass", null)).isFalse();
    }
}
