package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.beans.PropertyDescriptor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Validates {@link ValidPartyDocument} by reading the bean's {@code documentType} and
 * {@code document} properties via reflection and applying the check-digit algorithm matching
 * the declared {@link DocumentType}.
 */
public class PartyDocumentValidator implements ConstraintValidator<ValidPartyDocument, Object> {

    /**
     * Reads the {@code documentType} and {@code document} properties from the given bean and
     * validates the document against the CPF or CNPJ check-digit algorithm matching the declared
     * type. A {@code null} bean, or either property resolving to {@code null}, is considered valid.
     *
     * @param value   the bean instance to validate
     * @param context the constraint validator context (unused)
     * @return {@code true} if the bean is {@code null}, either property is {@code null}, or the
     *         document is valid for the declared type; {@code false} otherwise
     * @throws IllegalStateException if either property is not readable on the bean's class
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        var documentType = (DocumentType) readProperty(value, "documentType");
        var document = (String) readProperty(value, "document");
        if (documentType == null || document == null) {
            return true;
        }

        return switch (documentType) {
            case CPF -> BrazilianDocumentValidation.isValidCpf(document);
            case CNPJ -> BrazilianDocumentValidation.isValidCnpj(document);
        };
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
