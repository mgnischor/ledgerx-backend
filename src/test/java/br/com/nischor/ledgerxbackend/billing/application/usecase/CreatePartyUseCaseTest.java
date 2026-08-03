package br.com.nischor.ledgerxbackend.billing.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.nischor.ledgerxbackend.billing.application.dto.PartyDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.PartyMapper;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePartyUseCaseTest {

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private PartyMapper partyMapper;

    private CreatePartyUseCase useCase;

    private final UUID companyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CreatePartyUseCase(partyRepository, partyMapper);
    }

    @Test
    void createsAndPersistsParty() {
        when(partyRepository.save(any(Party.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var dto = new PartyDto(UUID.randomUUID(), companyId, "Jane Doe", "11144477735", "jane@example.com",
                PartyType.CUSTOMER);
        when(partyMapper.toDto(any(Party.class))).thenReturn(dto);

        var result = useCase.execute(companyId, "Jane Doe", DocumentNumber.cpf("11144477735"), "jane@example.com",
                PartyType.CUSTOMER);

        assertThat(result).isEqualTo(dto);
    }
}
