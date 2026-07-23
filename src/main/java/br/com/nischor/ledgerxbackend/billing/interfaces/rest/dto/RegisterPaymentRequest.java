package br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterPaymentRequest(@NotNull UUID installmentId, @NotNull @PastOrPresent LocalDate paidOn) {
}
