package br.com.nischor.ledgerxbackend.billing.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InvoiceMapperTest {

    private final InvoiceMapper mapper = new InvoiceMapper();

    @Test
    void mapsAllFieldsIncludingInstallmentCount() {
        var installments = List.of(
                new Installment(UUID.randomUUID(), 1, Money.brl(new BigDecimal("100.00")), LocalDate.now()),
                new Installment(UUID.randomUUID(), 2, Money.brl(new BigDecimal("100.00")), LocalDate.now().plusMonths(1)));
        var invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), PartyType.CUSTOMER,
                installments);

        var dto = mapper.toDto(invoice);

        assertThat(dto.id()).isEqualTo(invoice.getId());
        assertThat(dto.companyId()).isEqualTo(invoice.getCompanyId());
        assertThat(dto.partyId()).isEqualTo(invoice.getPartyId());
        assertThat(dto.direction()).isEqualTo(PartyType.CUSTOMER);
        assertThat(dto.status()).isEqualTo(InvoiceStatus.OPEN);
        assertThat(dto.installmentCount()).isEqualTo(2);
    }
}
