package br.com.nischor.ledgerxbackend.company.application.usecase;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.mapper.CompanyMapper;
import br.com.nischor.ledgerxbackend.company.domain.exception.CompanyAlreadyRegisteredException;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Use case that registers a new company after checking that its CNPJ is not already registered.
 */
@Service
public class RegisterCompanyUseCase {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    /**
     * Creates the use case with its required collaborators.
     *
     * @param companyRepository repository used to check existence and persist companies
     * @param companyMapper mapper used to convert the domain company to a DTO
     */
    public RegisterCompanyUseCase(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    /**
     * Validates the CNPJ, ensures it is not already registered, and creates a new company.
     *
     * @param legalName registered legal name of the company
     * @param tradeName trade (fantasy) name of the company
     * @param rawCnpj CNPJ document number as plain text, to be parsed and validated
     * @param size company size classification
     * @param address address of the company
     * @return the newly registered company as a DTO
     * @throws CompanyAlreadyRegisteredException if a company with the same CNPJ already exists
     */
    public CompanyDto execute(String legalName, String tradeName, String rawCnpj, CompanySize size,
            Address address) {
        var cnpj = DocumentNumber.cnpj(rawCnpj);
        if (companyRepository.existsByCnpj(cnpj)) {
            throw new CompanyAlreadyRegisteredException(rawCnpj);
        }

        var company = new Company(UUID.randomUUID(), legalName, tradeName, cnpj, size, address);
        return companyMapper.toDto(companyRepository.save(company));
    }
}
