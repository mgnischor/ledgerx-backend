package br.com.nischor.ledgerxbackend.accounting.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-layer data transfer object representing a financial account.
 *
 * @param id the financial account identifier
 * @param companyId the identifier of the company that owns the account
 * @param name the account name
 * @param balance the current account balance
 * @param currency the ISO currency code of the balance
 * @param active whether the account is currently active
 */
public record FinancialAccountDto(UUID id, UUID companyId, String name, BigDecimal balance, String currency,
        boolean active) {
}
