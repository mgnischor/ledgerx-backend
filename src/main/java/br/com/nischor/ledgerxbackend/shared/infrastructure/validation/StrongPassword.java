package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constrains a string to contain at least one uppercase letter, one lowercase letter, one digit,
 * and one special (non-alphanumeric) character, enforced by {@link StrongPasswordValidator}.
 * Note this constraint does not enforce a minimum length; combine with {@code @Size} if needed.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {

    /**
     * The error message reported when the password does not meet the strength requirements.
     *
     * @return the validation error message template
     */
    String message() default "password must contain at least one uppercase letter, one lowercase letter, "
            + "one digit and one special character";

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
