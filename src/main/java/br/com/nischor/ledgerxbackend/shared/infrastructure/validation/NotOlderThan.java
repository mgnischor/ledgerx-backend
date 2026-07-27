package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constrains a {@link java.time.LocalDate} value to be no more than a given number of years in
 * the past (i.e. not older than {@link #years()} years ago), enforced by
 * {@link NotOlderThanValidator}. Typically used to bound date-of-birth or founding-date fields.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotOlderThanValidator.class)
public @interface NotOlderThan {

    /**
     * The maximum age, in years, that the annotated date may represent.
     *
     * @return the maximum allowed age in years
     */
    int years();

    /**
     * The error message reported when the date is older than allowed.
     *
     * @return the validation error message template
     */
    String message() default "date must not be older than {years} years";

    /**
     * Validation groups this constraint belongs to.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload types that can be attached to this constraint.
     *
     * @return the payload types
     */
    Class<? extends Payload>[] payload() default {};
}
