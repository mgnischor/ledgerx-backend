package br.com.nischor.ledgerxbackend.billing.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;

/**
 * Represents a party (customer or supplier) that an {@link Invoice} can be issued to or received from.
 */
public class Party {

    private final UUID id;
    private final UUID companyId;
    private String name;
    private DocumentNumber document;
    private EmailAddress email;
    private PartyType type;

    /**
     * Creates a new party.
     *
     * @param id        the party identifier.
     * @param companyId the identifier of the company the party belongs to.
     * @param name      the party's display name.
     * @param document  the party's document number.
     * @param email     the party's email address.
     * @param type      whether the party is a customer or a supplier.
     */
    public Party(UUID id, UUID companyId, String name, DocumentNumber document, EmailAddress email, PartyType type) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.document = document;
        this.email = email;
        this.type = type;
    }

    /**
     * Changes this party's display name.
     *
     * @param name the new name.
     */
    public void rename(String name) {
        this.name = name;
    }

    /**
     * @return the party identifier.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the identifier of the company the party belongs to.
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * @return the party's display name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the party's document number.
     */
    public DocumentNumber getDocument() {
        return document;
    }

    /**
     * @return the party's email address.
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * @return whether the party is a customer or a supplier.
     */
    public PartyType getType() {
        return type;
    }
}
