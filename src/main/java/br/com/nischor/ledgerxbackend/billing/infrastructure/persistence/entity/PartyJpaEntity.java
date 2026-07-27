package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA entity mapping a customer or supplier (party) to the {@code parties} table.
 */
@Entity
@Table(name = "parties")
public class PartyJpaEntity extends AuditableEntity {

    /** Identifier of the company this party belongs to. */
    @Column(nullable = false)
    private UUID companyId;

    /** Display name of the party. */
    @Column(nullable = false)
    private String name;

    /** Raw document number (CPF or CNPJ) of the party. */
    @Column(nullable = false)
    private String document;

    /** Contact email address of the party. */
    @Column(nullable = false)
    private String email;

    /** Whether the party is a customer or a supplier. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType type;

    /** JPA-only default constructor. */
    protected PartyJpaEntity() {
    }

    /**
     * Creates a new party.
     *
     * @param id the party identifier
     * @param companyId the identifier of the company the party belongs to
     * @param name the display name of the party
     * @param document the raw document number (CPF or CNPJ) of the party
     * @param email the contact email address of the party
     * @param type whether the party is a customer or a supplier
     */
    public PartyJpaEntity(UUID id, UUID companyId, String name, String document, String email, PartyType type) {
        super(id);
        this.companyId = companyId;
        this.name = name;
        this.document = document;
        this.email = email;
        this.type = type;
    }

    /**
     * Returns the identifier of the company this party belongs to.
     *
     * @return the company identifier
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * Returns the display name of this party.
     *
     * @return the party name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the raw document number (CPF or CNPJ) of this party.
     *
     * @return the document number
     */
    public String getDocument() {
        return document;
    }

    /**
     * Returns the contact email address of this party.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns whether this party is a customer or a supplier.
     *
     * @return the party type
     */
    public PartyType getType() {
        return type;
    }
}
