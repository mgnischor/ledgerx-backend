package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotOlderThanValidator.class)
public @interface NotOlderThan {

    int years();

    String message() default "date must not be older than {years} years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
