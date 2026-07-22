package br.com.nischor.ledgerxbackend.billing.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import java.util.UUID;

public class Party {

    private final UUID id;
    private final UUID companyId;
    private String name;
    private DocumentNumber document;
    private EmailAddress email;
    private PartyType type;

    public Party(UUID id, UUID companyId, String name, DocumentNumber document, EmailAddress email, PartyType type) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.document = document;
        this.email = email;
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

    public DocumentNumber getDocument() {
        return document;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public PartyType getType() {
        return type;
    }
}
