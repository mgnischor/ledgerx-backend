package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates {@link StrongPassword} by checking that the string contains at least one uppercase
 * letter, one lowercase letter, one digit, and one non-alphanumeric character.
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    /**
     * Checks whether the given string contains at least one uppercase letter, one lowercase
     * letter, one digit, and one special (non-alphanumeric) character. A {@code null} value is
     * considered valid (use {@code @NotNull} separately to require presence).
     *
     * @param value   the candidate password
     * @param context the constraint validator context (unused)
     * @return {@code true} if {@code value} is {@code null} or meets all strength requirements,
     *         {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean hasUppercase = value.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = value.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = value.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = value.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}
