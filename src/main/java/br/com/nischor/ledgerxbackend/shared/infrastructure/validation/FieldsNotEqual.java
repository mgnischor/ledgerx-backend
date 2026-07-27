package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint asserting that two named bean properties do not hold the same value
 * (compared case-insensitively, after trimming), enforced by {@link FieldsNotEqualValidator}.
 * Typically used to prevent a new value from being identical to a current one (e.g. changing a
 * password to the same password).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsNotEqualValidator.class)
public @interface FieldsNotEqual {

    /**
     * The name of the first bean property to compare.
     *
     * @return the first property name
     */
    String first();

    /**
     * The name of the second bean property to compare.
     *
     * @return the second property name
     */
    String second();

    /**
     * The error message reported when both properties hold the same value.
     *
     * @return the validation error message template
     */
    String message() default "these two fields must not have the same value";

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
