package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || BrazilianDocumentValidation.isValidCpf(value);
    }
}
