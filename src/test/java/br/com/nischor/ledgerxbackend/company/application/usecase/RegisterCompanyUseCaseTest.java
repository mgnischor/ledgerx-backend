package br.com.nischor.ledgerxbackend.company.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.company.application.dto.CompanyDto;
import br.com.nischor.ledgerxbackend.company.application.mapper.CompanyMapper;
import br.com.nischor.ledgerxbackend.company.domain.exception.CompanyAlreadyRegisteredException;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterCompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    private RegisterCompanyUseCase useCase;

    private final Address address = new Address("Main St", "100", "Sao Paulo", "SP", "01310-100", "Brazil");

    @BeforeEach
    void setUp() {
        useCase = new RegisterCompanyUseCase(companyRepository, companyMapper);
    }

    @Test
    void registersCompanyWithUnusedCnpj() {
        when(companyRepository.existsByCnpj(any())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new CompanyDto(UUID.randomUUID(), "Acme Ltda", "Acme", "11222333000181", CompanySize.MICRO, true);
        when(companyMapper.toDto(any(Company.class))).thenReturn(dto);

        var result = useCase.execute("Acme Ltda", "Acme", "11.222.333/0001-81", CompanySize.MICRO, address);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void rejectsAlreadyRegisteredCnpj() {
        when(companyRepository.existsByCnpj(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("Acme Ltda", "Acme", "11.222.333/0001-81", CompanySize.MICRO,
                address)).isInstanceOf(CompanyAlreadyRegisteredException.class);
        verify(companyRepository, never()).save(any());
    }
}
