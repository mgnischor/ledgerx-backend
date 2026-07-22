package br.com.nischor.ledgerxbackend.shared.domain.valueobject;

import java.io.Serializable;

public record DocumentNumber(String value, DocumentType type) implements Serializable {

    public DocumentNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Document number must not be blank");
        }
        value = value.replaceAll("\\D", "");
        if (type == DocumentType.CPF && value.length() != 11) {
            throw new IllegalArgumentException("CPF must have 11 digits");
        }
        if (type == DocumentType.CNPJ && value.length() != 14) {
            throw new IllegalArgumentException("CNPJ must have 14 digits");
        }
    }

    public static DocumentNumber cpf(String value) {
        return new DocumentNumber(value, DocumentType.CPF);
    }

    public static DocumentNumber cnpj(String value) {
        return new DocumentNumber(value, DocumentType.CNPJ);
    }

    public enum DocumentType {
        CPF,
        CNPJ
    }
}
