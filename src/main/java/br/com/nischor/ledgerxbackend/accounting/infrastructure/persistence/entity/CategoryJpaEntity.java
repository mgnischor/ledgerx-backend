package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    protected CategoryJpaEntity() {
    }

    public CategoryJpaEntity(UUID companyId, String name, TransactionType type) {
        this.companyId = companyId;
        this.name = name;
        this.type = type;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public TransactionType getType() {
        return type;
    }
}
