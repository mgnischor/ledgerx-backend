package br.com.nischor.ledgerxbackend.company.domain.model;

import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import java.util.UUID;

/**
 * Domain entity representing a company (tenant) registered in the system, identified by its
 * CNPJ, with a legal/trade name, a size classification, an address, and an active status.
 */
public class Company {

    /** Unique identifier of the company. */
    private final UUID id;
    private String legalName;
    private String tradeName;
    /** Brazilian CNPJ document number identifying the company. */
    private DocumentNumber cnpj;
    private CompanySize size;
    private Address address;
    /** Whether the company is currently active. */
    private boolean active;

    /**
     * Creates a new company, active by default.
     *
     * @param id unique identifier of the company
     * @param legalName registered legal name of the company
     * @param tradeName trade (fantasy) name of the company
     * @param cnpj CNPJ document number identifying the company
     * @param size company size classification
     * @param address address of the company
     */
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

    /**
     * Updates the company's address.
     *
     * @param newAddress the new address to assign
     */
    public void relocate(Address newAddress) {
        this.address = newAddress;
    }

    /**
     * Updates the company's legal and trade names.
     *
     * @param legalName new registered legal name
     * @param tradeName new trade (fantasy) name
     */
    public void rename(String legalName, String tradeName) {
        this.legalName = legalName;
        this.tradeName = tradeName;
    }

    /**
     * Marks the company as inactive.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Returns the company's unique identifier.
     *
     * @return the company id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the company's registered legal name.
     *
     * @return the legal name
     */
    public String getLegalName() {
        return legalName;
    }

    /**
     * Returns the company's trade (fantasy) name.
     *
     * @return the trade name
     */
    public String getTradeName() {
        return tradeName;
    }

    /**
     * Returns the company's CNPJ document number.
     *
     * @return the CNPJ
     */
    public DocumentNumber getCnpj() {
        return cnpj;
    }

    /**
     * Returns the company's size classification.
     *
     * @return the company size
     */
    public CompanySize getSize() {
        return size;
    }

    /**
     * Returns the company's address.
     *
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Indicates whether the company is currently active.
     *
     * @return {@code true} if the company is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }
}
