package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST request payload for creating a category.
 *
 * @param name the category name; required, up to 60 characters
 * @param type the transaction type (income, expense, or transfer) the category is associated with
 */
public record CreateCategoryRequest(@NotBlank @Size(max = 60) String name, @NotNull TransactionType type) {
}
