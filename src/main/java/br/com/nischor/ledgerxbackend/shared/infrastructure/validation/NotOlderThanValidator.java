package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Validates {@link NotOlderThan} by checking that the annotated date is not before
 * {@code LocalDate.now().minusYears(years)}.
 */
public class NotOlderThanValidator implements ConstraintValidator<NotOlderThan, LocalDate> {

    private int years;

    /**
     * Captures the maximum allowed age, in years, declared on the {@link NotOlderThan}
     * annotation instance.
     *
     * @param constraintAnnotation the annotation instance being processed
     */
    @Override
    public void initialize(NotOlderThan constraintAnnotation) {
        this.years = constraintAnnotation.years();
    }

    /**
     * Checks whether the given date is no older than the configured number of years. A
     * {@code null} value is considered valid (use {@code @NotNull} separately to require presence).
     *
     * @param value   the candidate date
     * @param context the constraint validator context (unused)
     * @return {@code true} if {@code value} is {@code null} or not before today minus {@code years} years,
     *         {@code false} otherwise
     */
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(LocalDate.now().minusYears(years));
    }
}
