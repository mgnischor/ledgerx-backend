package br.com.nischor.ledgerxbackend.company.application.dto;

import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import java.util.UUID;

/**
 * Application-layer data transfer object exposing a {@code Company}'s public data.
 *
 * @param id identifier of the company
 * @param legalName registered legal name of the company
 * @param tradeName trade (fantasy) name of the company
 * @param cnpj Brazilian CNPJ document number as plain text
 * @param size company size classification
 * @param active whether the company is currently active
 */
public record CompanyDto(UUID id, String legalName, String tradeName, String cnpj, CompanySize size,
        boolean active) {
}
