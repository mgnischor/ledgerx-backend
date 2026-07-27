package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a string is a structurally valid CNPJ (Brazilian company taxpayer registry
 * number), as declared by {@link ValidCnpj}.
 */
public class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {

    /**
     * Checks whether the given value is a valid CNPJ using
     * {@link BrazilianDocumentValidation#isValidCnpj(String)}. A {@code null} value is considered
     * valid (use {@code @NotNull} separately to require presence).
     *
     * @param value   the candidate CNPJ value, digits only or formatted
     * @param context the constraint validator context (unused)
     * @return {@code true} if {@code value} is {@code null} or a structurally valid CNPJ, {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || BrazilianDocumentValidation.isValidCnpj(value);
    }
}
