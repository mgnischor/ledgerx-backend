package br.com.nischor.ledgerxbackend.company.application.usecase;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.mapper.CompanyMapper;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeactivateCompanyUseCase {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public DeactivateCompanyUseCase(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public CompanyDto execute(UUID companyId) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(Company.class, companyId));
        company.deactivate();
        return companyMapper.toDto(companyRepository.save(company));
    }
}
