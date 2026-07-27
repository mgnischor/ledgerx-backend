package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constrains a string to be one of the 27 official Brazilian state/federal-district abbreviations
 * (UF codes, e.g. "SP", "RJ"), enforced by {@link BrazilianStateValidator}.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BrazilianStateValidator.class)
public @interface ValidBrazilianState {

    /**
     * The error message reported when the value is not a valid Brazilian state code.
     *
     * @return the validation error message template
     */
    String message() default "invalid Brazilian state (UF)";

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
