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

@Entity
@Table(name = "invoices")
public class InvoiceJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID partyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.OPEN;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstallmentJpaEntity> installments = new ArrayList<>();

    protected InvoiceJpaEntity() {
    }

    public InvoiceJpaEntity(UUID companyId, UUID partyId, PartyType direction) {
        this.companyId = companyId;
        this.partyId = partyId;
        this.direction = direction;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public PartyType getDirection() {
        return direction;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public List<InstallmentJpaEntity> getInstallments() {
        return installments;
    }
}
