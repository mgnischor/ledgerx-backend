package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotOlderThanValidatorTest {

    /** Carries a real {@link NotOlderThan} annotation instance for the validator under test to read. */
    private static final class Holder {
        @NotOlderThan(years = 5)
        LocalDate occurredOn;
    }

    private NotOlderThanValidator validator;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        validator = new NotOlderThanValidator();
        Field field = Holder.class.getDeclaredField("occurredOn");
        validator.initialize(field.getAnnotation(NotOlderThan.class));
    }

    @Test
    void acceptsNullValue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsTodaysDate() {
        assertThat(validator.isValid(LocalDate.now(), null)).isTrue();
    }

    @Test
    void acceptsDateWithinAllowedYears() {
        assertThat(validator.isValid(LocalDate.now().minusYears(5), null)).isTrue();
    }

    @Test
    void rejectsDateOlderThanAllowedYears() {
        assertThat(validator.isValid(LocalDate.now().minusYears(5).minusDays(1), null)).isFalse();
    }
}
