package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Validates that a string is one of the 27 official Brazilian state/federal-district abbreviations
 * (UF codes), as declared by {@link ValidBrazilianState}.
 */
public class BrazilianStateValidator implements ConstraintValidator<ValidBrazilianState, String> {

    private static final Set<String> VALID_STATES = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE",
            "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    /**
     * Checks whether the given value, upper-cased, matches one of the 27 valid Brazilian state
     * abbreviations. A {@code null} value is considered valid (use {@code @NotNull} separately to
     * require presence).
     *
     * @param value   the candidate state abbreviation
     * @param context the constraint validator context (unused)
     * @return {@code true} if {@code value} is {@code null} or a valid UF code, {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || VALID_STATES.contains(value.toUpperCase());
    }
}
