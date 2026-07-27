package br.com.nischor.ledgerxbackend.company.application.usecase;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.mapper.CompanyMapper;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Use case that deactivates an existing company.
 */
@Service
public class DeactivateCompanyUseCase {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    /**
     * Creates the use case with its required collaborators.
     *
     * @param companyRepository repository used to load and persist companies
     * @param companyMapper mapper used to convert the domain company to a DTO
     */
    public DeactivateCompanyUseCase(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    /**
     * Deactivates the company identified by the given id.
     *
     * @param companyId identifier of the company to deactivate
     * @return the deactivated company as a DTO
     * @throws br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException if no
     *         company exists with the given id
     */
    public CompanyDto execute(UUID companyId) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(Company.class, companyId));
        company.deactivate();
        return companyMapper.toDto(companyRepository.save(company));
    }
}
