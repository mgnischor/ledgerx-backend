package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA entity persisting a {@code Category} to the {@code categories} table.
 */
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

    /**
     * Protected no-args constructor required by JPA.
     */
    protected CategoryJpaEntity() {
    }

    /**
     * Creates a new category entity.
     *
     * @param id the category identifier
     * @param companyId the identifier of the company that owns the category
     * @param name the category name
     * @param type the transaction type (income or expense) the category is associated with
     */
    public CategoryJpaEntity(UUID id, UUID companyId, String name, TransactionType type) {
        super(id);
        this.companyId = companyId;
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the identifier of the company that owns the category.
     *
     * @return the company identifier
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * Returns the category name.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the transaction type the category is associated with.
     *
     * @return the transaction type
     */
    public TransactionType getType() {
        return type;
    }
}
