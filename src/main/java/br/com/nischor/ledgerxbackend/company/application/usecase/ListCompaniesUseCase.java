package br.com.nischor.ledgerxbackend.company.application.usecase;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.mapper.CompanyMapper;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Use case that lists every registered company (tenant). Access is governed purely by the
 * caller's {@code PERMISSION_READ} authority — companies have no per-user membership, so any
 * authorized caller sees every company.
 */
@Service
public class ListCompaniesUseCase {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    /**
     * Creates the use case with its required collaborators.
     *
     * @param companyRepository repository used to retrieve companies
     * @param companyMapper mapper used to convert domain companies to DTOs
     */
    public ListCompaniesUseCase(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    /**
     * Retrieves every registered company.
     *
     * @return all companies as DTOs
     */
    public List<CompanyDto> execute() {
        return companyRepository.findAll().stream().map(companyMapper::toDto).toList();
    }
}
