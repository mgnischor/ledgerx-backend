package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.shared.infrastructure.validation.FieldsNotEqual;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST request payload for transferring funds between two financial accounts. Class-level validation ensures
 * the source and destination accounts are different.
 *
 * @param fromAccountId the identifier of the account funds are debited from
 * @param toAccountId the identifier of the account funds are credited to
 * @param amount the amount to transfer; must be strictly positive
 */
@FieldsNotEqual(first = "fromAccountId", second = "toAccountId",
        message = "the source and destination accounts must be different")
public record TransferFundsRequest(
        @NotNull UUID fromAccountId,
        @NotNull UUID toAccountId,
        @NotNull @Positive BigDecimal amount) {
}
