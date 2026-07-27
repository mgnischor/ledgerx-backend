package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.billing.domain.model.InvoiceStatus;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity mapping an accounts receivable/payable invoice to the {@code invoices} table,
 * including its status and the {@link InstallmentJpaEntity} rows composing it.
 */
@Entity
@Table(name = "invoices")
public class InvoiceJpaEntity extends AuditableEntity {

    /** Identifier of the company that owns this invoice. */
    @Column(nullable = false)
    private UUID companyId;

    /** Identifier of the counterparty (customer or supplier) of this invoice. */
    @Column(nullable = false)
    private UUID partyId;

    /** Whether this invoice is receivable or payable. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType direction;

    /** Current lifecycle status of the invoice. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.OPEN;

    /** Installments composing this invoice. */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstallmentJpaEntity> installments = new ArrayList<>();

    /** JPA-only default constructor. */
    protected InvoiceJpaEntity() {
    }

    /**
     * Creates a new invoice with no installments and status {@link InvoiceStatus#OPEN}.
     *
     * @param id the invoice identifier
     * @param companyId the identifier of the company that owns the invoice
     * @param partyId the identifier of the counterparty of the invoice
     * @param direction whether the invoice is receivable or payable
     */
    public InvoiceJpaEntity(UUID id, UUID companyId, UUID partyId, PartyType direction) {
        super(id);
        this.companyId = companyId;
        this.partyId = partyId;
        this.direction = direction;
    }

    /**
     * Returns the identifier of the company that owns this invoice.
     *
     * @return the company identifier
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * Returns the identifier of the counterparty of this invoice.
     *
     * @return the party identifier
     */
    public UUID getPartyId() {
        return partyId;
    }

    /**
     * Returns whether this invoice is receivable or payable.
     *
     * @return the invoice direction
     */
    public PartyType getDirection() {
        return direction;
    }

    /**
     * Returns the current lifecycle status of this invoice.
     *
     * @return the invoice status
     */
    public InvoiceStatus getStatus() {
        return status;
    }

    /**
     * Updates the lifecycle status of this invoice.
     *
     * @param status the new invoice status
     */
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    /**
     * Returns the installments composing this invoice.
     *
     * @return the mutable list of installments
     */
    public List<InstallmentJpaEntity> getInstallments() {
        return installments;
    }
}
