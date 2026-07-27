package br.com.nischor.ledgerxbackend.company.application.mapper;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Company} domain objects into {@link CompanyDto} application objects.
 */
@Component
public class CompanyMapper {

    /**
     * Builds a {@link CompanyDto} from the given domain {@link Company}.
     *
     * @param company the domain company to convert
     * @return the corresponding data transfer object
     */
    public CompanyDto toDto(Company company) {
        return new CompanyDto(company.getId(), company.getLegalName(), company.getTradeName(),
                company.getCnpj().value(), company.getSize(), company.isActive());
    }
}
