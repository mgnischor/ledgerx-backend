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

@Service
public class RegisterCompanyUseCase {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public RegisterCompanyUseCase(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

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
