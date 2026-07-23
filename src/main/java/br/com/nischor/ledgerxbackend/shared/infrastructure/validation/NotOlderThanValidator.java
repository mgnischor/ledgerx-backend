package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotOlderThanValidator implements ConstraintValidator<NotOlderThan, LocalDate> {

    private int years;

    @Override
    public void initialize(NotOlderThan constraintAnnotation) {
        this.years = constraintAnnotation.years();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(LocalDate.now().minusYears(years));
    }
}
