package br.com.nischor.ledgerxbackend.company.domain.model;

import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.UUID;

public class Company {

    private final UUID id;
    private String legalName;
    private String tradeName;
    private DocumentNumber cnpj;
    private CompanySize size;
    private Address address;
    private boolean active;

    public Company(UUID id, String legalName, String tradeName, DocumentNumber cnpj, CompanySize size,
            Address address) {
        this.id = id;
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.size = size;
        this.address = address;
        this.active = true;
    }

    public void relocate(Address newAddress) {
        this.address = newAddress;
    }

    public void rename(String legalName, String tradeName) {
        this.legalName = legalName;
        this.tradeName = tradeName;
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public DocumentNumber getCnpj() {
        return cnpj;
    }

    public CompanySize getSize() {
        return size;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }
}
