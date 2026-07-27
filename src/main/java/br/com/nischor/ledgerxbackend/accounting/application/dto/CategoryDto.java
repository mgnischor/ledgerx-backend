package br.com.nischor.ledgerxbackend.accounting.application.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import java.util.UUID;

/**
 * Application-layer data transfer object representing a transaction category.
 *
 * @param id the category identifier
 * @param companyId the identifier of the company that owns the category
 * @param name the category name
 * @param type the transaction type (income or expense) the category is associated with
 */
public record CategoryDto(UUID id, UUID companyId, String name, TransactionType type) {
}
