package br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull UUID companyId,
        @NotNull UUID partyId,
        @NotNull PartyType direction,
        @NotEmpty List<BigDecimal> installmentAmounts,
        @NotNull LocalDate firstDueDate) {
}
