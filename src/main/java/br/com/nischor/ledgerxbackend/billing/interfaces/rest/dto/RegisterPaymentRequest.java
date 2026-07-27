package br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload to register a payment for one of an invoice's installments.
 *
 * @param installmentId identifier of the installment being paid; must not be {@code null}
 * @param paidOn date the payment was made; must not be {@code null} and not be in the future
 */
public record RegisterPaymentRequest(@NotNull UUID installmentId, @NotNull @PastOrPresent LocalDate paidOn) {
}
