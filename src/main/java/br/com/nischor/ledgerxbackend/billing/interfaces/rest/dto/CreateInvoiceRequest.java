package br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull UUID companyId,
        @NotNull UUID partyId,
        @NotNull PartyType direction,
        @NotEmpty @Size(max = 60) List<BigDecimal> installmentAmounts,
        @NotNull @FutureOrPresent LocalDate firstDueDate) {
}
