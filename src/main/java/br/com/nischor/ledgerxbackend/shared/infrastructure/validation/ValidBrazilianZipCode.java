package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constrains a string to match the Brazilian ZIP code (CEP) format {@code NNNNN-NNN} (the hyphen
 * is optional). Unlike the other custom constraints in this package, this one has no dedicated
 * {@link jakarta.validation.ConstraintValidator} ({@code validatedBy = {}}); it is a composed
 * constraint that delegates entirely to the meta-annotated {@link Pattern} constraint.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "invalid Brazilian zip code (CEP), expected format NNNNN-NNN")
public @interface ValidBrazilianZipCode {

    /**
     * The error message reported when the value does not match the CEP format.
     *
     * @return the validation error message template
     */
    String message() default "invalid Brazilian zip code (CEP), expected format NNNNN-NNN";

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
