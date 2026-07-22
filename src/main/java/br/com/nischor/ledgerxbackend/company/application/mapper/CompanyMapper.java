package br.com.nischor.ledgerxbackend.company.application.mapper;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        return new CompanyDto(company.getId(), company.getLegalName(), company.getTradeName(),
                company.getCnpj().value(), company.getSize(), company.isActive());
    }
}
