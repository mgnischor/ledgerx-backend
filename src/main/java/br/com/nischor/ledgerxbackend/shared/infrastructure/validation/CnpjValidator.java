package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || BrazilianDocumentValidation.isValidCnpj(value);
    }
}
