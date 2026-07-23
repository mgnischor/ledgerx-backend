package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.beans.PropertyDescriptor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

public class PartyDocumentValidator implements ConstraintValidator<ValidPartyDocument, Object> {

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

    private static Object readProperty(Object bean, String propertyName) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if (descriptor == null || descriptor.getReadMethod() == null) {
            throw new IllegalStateException("No readable property '%s' on %s".formatted(propertyName,
                    bean.getClass()));
        }
        return ReflectionUtils.invokeMethod(descriptor.getReadMethod(), bean);
    }
}
