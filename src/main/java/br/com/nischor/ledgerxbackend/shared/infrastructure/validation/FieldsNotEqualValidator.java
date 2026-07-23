package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.beans.PropertyDescriptor;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

public class FieldsNotEqualValidator implements ConstraintValidator<FieldsNotEqual, Object> {

    private String first;
    private String second;

    @Override
    public void initialize(FieldsNotEqual constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object firstValue = readProperty(value, first);
        Object secondValue = readProperty(value, second);

        if (firstValue == null || secondValue == null) {
            return true;
        }

        return !Objects.equals(String.valueOf(firstValue).trim().toLowerCase(),
                String.valueOf(secondValue).trim().toLowerCase());
    }

    private static Object readProperty(Object bean, String propertyName) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if (descriptor == null || descriptor.getReadMethod() == null) {
            throw new IllegalStateException("No readable property '%s' on %s".formatted(propertyName,
                    bean.getClass()));
        }
        return ReflectionUtils.invokeMethod(descriptor.getReadMethod(), bean);
    }
}
