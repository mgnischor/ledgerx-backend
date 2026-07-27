package br.com.nischor.ledgerxbackend.billing.application.dto;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import java.util.UUID;

/**
 * Data transfer object exposing a read-only, application-layer view of a {@code Party}.
 *
 * @param id        the party identifier.
 * @param companyId the identifier of the company the party belongs to.
 * @param name      the party's display name.
 * @param document  the party's document number, in plain string form.
 * @param email     the party's email address, in plain string form.
 * @param type      whether the party is a customer or a supplier.
 */
public record PartyDto(UUID id, UUID companyId, String name, String document, String email, PartyType type) {
}
