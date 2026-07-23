package br.com.nischor.ledgerxbackend.accounting.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "financial_accounts")
public class FinancialAccountJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private boolean active = true;

    protected FinancialAccountJpaEntity() {
    }

    public FinancialAccountJpaEntity(UUID id, UUID companyId, String name, BigDecimal balance, String currencyCode) {
        super(id);
        this.companyId = companyId;
        this.name = name;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public boolean isActive() {
        return active;
    }
}
