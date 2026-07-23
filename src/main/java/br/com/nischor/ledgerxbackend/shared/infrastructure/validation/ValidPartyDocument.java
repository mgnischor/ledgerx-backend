package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint for DTOs exposing a {@code documentType} (CPF/CNPJ) and a
 * {@code document} field, validating the document against the check-digit algorithm that
 * matches the declared type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PartyDocumentValidator.class)
public @interface ValidPartyDocument {

    String message() default "document does not match a valid CPF/CNPJ for the declared document type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
