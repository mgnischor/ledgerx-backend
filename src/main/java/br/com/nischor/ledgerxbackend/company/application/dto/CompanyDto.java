package br.com.nischor.ledgerxbackend.company.application.dto;

import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import java.util.UUID;

public record CompanyDto(UUID id, String legalName, String tradeName, String cnpj, CompanySize size,
        boolean active) {
}
