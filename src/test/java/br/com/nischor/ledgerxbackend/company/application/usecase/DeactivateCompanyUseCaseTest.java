package br.com.nischor.ledgerxbackend.company.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.mapper.CompanyMapper;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeactivateCompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    private DeactivateCompanyUseCase useCase;

    private final UUID companyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DeactivateCompanyUseCase(companyRepository, companyMapper);
    }

    @Test
    void deactivatesExistingCompany() {
        var company = new Company(companyId, "Acme Ltda", "Acme", DocumentNumber.cnpj("11222333000181"),
                CompanySize.MICRO, new Address("Main St", "100", "Sao Paulo", "SP", "01310-100", "Brazil"));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyRepository.save(company)).thenReturn(company);
        var dto = new CompanyDto(companyId, "Acme Ltda", "Acme", "11222333000181", CompanySize.MICRO, false);
        when(companyMapper.toDto(company)).thenReturn(dto);

        var result = useCase.execute(companyId);

        assertThat(result.active()).isFalse();
        assertThat(company.isActive()).isFalse();
    }

    @Test
    void rejectsUnknownCompany() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(companyId)).isInstanceOf(EntityNotFoundException.class);
    }
}
