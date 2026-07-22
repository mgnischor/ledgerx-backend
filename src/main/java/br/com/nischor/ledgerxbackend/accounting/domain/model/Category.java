package br.com.nischor.ledgerxbackend.accounting.domain.model;

import java.util.UUID;

public class Category {

    private final UUID id;
    private final UUID companyId;
    private String name;
    private TransactionType type;

    public Category(UUID id, UUID companyId, String name, TransactionType type) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.type = type;
    }

    public void rename(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
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
