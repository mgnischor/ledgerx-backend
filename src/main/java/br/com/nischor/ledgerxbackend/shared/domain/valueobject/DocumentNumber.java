package br.com.nischor.ledgerxbackend.shared.domain.valueobject;

import java.io.Serializable;

/**
 * Immutable value object representing a Brazilian document number (CPF or CNPJ).
 *
 * <p>The raw value has all non-digit characters stripped and its length is
 * validated according to the declared {@link DocumentType}. Note that this
 * value object only enforces digit count, not check-digit correctness.
 *
 * @param value the document number containing only digits
 * @param type  the type of document (CPF or CNPJ)
 */
public record DocumentNumber(String value, DocumentType type) implements Serializable {

    /**
     * Validates and normalizes the document number.
     *
     * @throws IllegalArgumentException if {@code value} is {@code null} or blank, or if the
     *                                   digit count does not match the expected length for the given {@code type}
     */
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

    /**
     * Creates a {@code DocumentNumber} of type {@link DocumentType#CPF}.
     *
     * @param value the raw CPF value, digits only or formatted
     * @return a new {@code DocumentNumber} instance of type CPF
     */
    public static DocumentNumber cpf(String value) {
        return new DocumentNumber(value, DocumentType.CPF);
    }

    /**
     * Creates a {@code DocumentNumber} of type {@link DocumentType#CNPJ}.
     *
     * @param value the raw CNPJ value, digits only or formatted
     * @return a new {@code DocumentNumber} instance of type CNPJ
     */
    public static DocumentNumber cnpj(String value) {
        return new DocumentNumber(value, DocumentType.CNPJ);
    }

    /**
     * The supported types of Brazilian document numbers.
     */
    public enum DocumentType {
        /** Cadastro de Pessoas Físicas: individual taxpayer registry number. */
        CPF,
        /** Cadastro Nacional da Pessoa Jurídica: company taxpayer registry number. */
        CNPJ
    }
}
