package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.beans.PropertyDescriptor;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Validates {@link FieldsNotEqual} by reading two named bean properties via reflection and
 * comparing their string representations, trimmed and lower-cased.
 */
public class FieldsNotEqualValidator implements ConstraintValidator<FieldsNotEqual, Object> {

    private String first;
    private String second;

    /**
     * Captures the property names declared on the {@link FieldsNotEqual} annotation instance.
     *
     * @param constraintAnnotation the annotation instance being processed
     */
    @Override
    public void initialize(FieldsNotEqual constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
    }

    /**
     * Reads the {@code first} and {@code second} properties from the given bean and checks that
     * their string values (trimmed, lower-cased) are not equal. A {@code null} bean, or either
     * property resolving to {@code null}, is considered valid.
     *
     * @param value   the bean instance to validate
     * @param context the constraint validator context (unused)
     * @return {@code true} if the properties differ (or either is absent), {@code false} if they are equal
     * @throws IllegalStateException if either property is not readable on the bean's class
     */
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

    /**
     * Reads the named readable property from the given bean via its JavaBean getter.
     *
     * @param bean         the bean instance to read from
     * @param propertyName the name of the property to read
     * @return the value returned by the property's getter
     * @throws IllegalStateException if no readable property with that name exists on the bean's class
     */
    private static Object readProperty(Object bean, String propertyName) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if (descriptor == null || descriptor.getReadMethod() == null) {
            throw new IllegalStateException("No readable property '%s' on %s".formatted(propertyName,
                    bean.getClass()));
        }
        return ReflectionUtils.invokeMethod(descriptor.getReadMethod(), bean);
    }
}
