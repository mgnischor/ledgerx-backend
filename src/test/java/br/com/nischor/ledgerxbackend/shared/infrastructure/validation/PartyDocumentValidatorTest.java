package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber.DocumentType;
import org.junit.jupiter.api.Test;

class PartyDocumentValidatorTest {

    public record DocumentHolder(DocumentType documentType, String document) {
    }

    private final PartyDocumentValidator validator = new PartyDocumentValidator();

    @Test
    void acceptsNullBean() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void acceptsValidCpfDeclaredAsCpf() {
        assertThat(validator.isValid(new DocumentHolder(DocumentType.CPF, "11144477735"), null)).isTrue();
    }

    @Test
    void acceptsValidCnpjDeclaredAsCnpj() {
        assertThat(validator.isValid(new DocumentHolder(DocumentType.CNPJ, "11222333000181"), null)).isTrue();
    }

    @Test
    void rejectsValidCpfDeclaredAsCnpj() {
        assertThat(validator.isValid(new DocumentHolder(DocumentType.CNPJ, "11144477735"), null)).isFalse();
    }

    @Test
    void rejectsInvalidCheckDigits() {
        assertThat(validator.isValid(new DocumentHolder(DocumentType.CPF, "11144477736"), null)).isFalse();
    }

    @Test
    void acceptsWhenDocumentTypeIsNull() {
        assertThat(validator.isValid(new DocumentHolder(null, "11144477735"), null)).isTrue();
    }

    @Test
    void acceptsWhenDocumentIsNull() {
        assertThat(validator.isValid(new DocumentHolder(DocumentType.CPF, null), null)).isTrue();
    }
}
