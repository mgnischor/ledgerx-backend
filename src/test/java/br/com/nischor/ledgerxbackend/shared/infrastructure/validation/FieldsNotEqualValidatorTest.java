package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldsNotEqualValidatorTest {

    @FieldsNotEqual(first = "first", second = "second")
    public record Pair(String first, String second) {
    }

    private FieldsNotEqualValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FieldsNotEqualValidator();
        validator.initialize(Pair.class.getAnnotation(FieldsNotEqual.class));
    }

    @Test
    void acceptsNullBean() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsDifferentValues() {
        assertThat(validator.isValid(new Pair("jane@example.com", "Str0ng!Pass"), null)).isTrue();
    }

    @Test
    void rejectsEqualValues() {
        assertThat(validator.isValid(new Pair("same", "same"), null)).isFalse();
    }

    @Test
    void rejectsEqualValuesIgnoringCaseAndWhitespace() {
        assertThat(validator.isValid(new Pair("  Same ", "same"), null)).isFalse();
    }

    @Test
    void acceptsWhenEitherValueIsNull() {
        assertThat(validator.isValid(new Pair(null, "same"), null)).isTrue();
    }

    @FieldsNotEqual(first = "nonExistent", second = "second")
    public record BadPair(String first, String second) {
    }

    @Test
    void rejectsUnknownProperty() {
        var validatorWithUnknownField = new FieldsNotEqualValidator();
        validatorWithUnknownField.initialize(BadPair.class.getAnnotation(FieldsNotEqual.class));

        assertThatThrownBy(() -> validatorWithUnknownField.isValid(new BadPair("a", "b"), null))
                .isInstanceOf(IllegalStateException.class);
    }
}
