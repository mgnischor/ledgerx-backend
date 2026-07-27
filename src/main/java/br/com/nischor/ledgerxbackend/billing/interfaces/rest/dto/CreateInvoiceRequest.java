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

/**
 * Request payload to issue a new invoice, carrying the owning company, counterparty, direction
 * (receivable/payable), the amounts of each installment and the first installment's due date.
 *
 * @param companyId identifier of the company issuing the invoice; must not be {@code null}
 * @param partyId identifier of the counterparty; must not be {@code null}
 * @param direction whether the invoice is receivable or payable; must not be {@code null}
 * @param installmentAmounts amounts of each installment, in order; must be non-empty and at most 60 entries
 * @param firstDueDate due date of the first installment; must not be {@code null} and not be in the past
 */
public record CreateInvoiceRequest(
        @NotNull UUID companyId,
        @NotNull UUID partyId,
        @NotNull PartyType direction,
        @NotEmpty @Size(max = 60) List<BigDecimal> installmentAmounts,
        @NotNull @FutureOrPresent LocalDate firstDueDate) {
}
