package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a string is a structurally valid CPF (Brazilian individual taxpayer registry
 * number), as declared by {@link ValidCpf}.
 */
public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    /**
     * Checks whether the given value is a valid CPF using
     * {@link BrazilianDocumentValidation#isValidCpf(String)}. A {@code null} value is considered
     * valid (use {@code @NotNull} separately to require presence).
     *
     * @param value   the candidate CPF value, digits only or formatted
     * @param context the constraint validator context (unused)
     * @return {@code true} if {@code value} is {@code null} or a structurally valid CPF, {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || BrazilianDocumentValidation.isValidCpf(value);
    }
}
