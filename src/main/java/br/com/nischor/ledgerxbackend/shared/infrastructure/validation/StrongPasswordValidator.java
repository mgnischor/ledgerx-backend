package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

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
