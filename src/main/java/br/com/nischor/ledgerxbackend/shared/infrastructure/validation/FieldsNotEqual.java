package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsNotEqualValidator.class)
public @interface FieldsNotEqual {

    String first();

    String second();

    String message() default "these two fields must not have the same value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
