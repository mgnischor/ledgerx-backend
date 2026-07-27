package br.com.nischor.ledgerxbackend.company.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA entity mapping a company (tenant) to the {@code companies} table, including its embedded
 * address and audit metadata inherited from {@link AuditableEntity}.
 */
@Entity
@Table(name = "companies")
public class CompanyJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false)
    private String tradeName;

    /** Brazilian CNPJ document number, stored as a unique plain string. */
    @Column(nullable = false, unique = true)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanySize size;

    @Embedded
    private AddressEmbeddable address;

    /** Whether the company is currently active. */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Default constructor required by JPA.
     */
    protected CompanyJpaEntity() {
    }

    /**
     * Creates a new company entity.
     *
     * @param id unique identifier of the company
     * @param legalName registered legal name of the company
     * @param tradeName trade (fantasy) name of the company
     * @param cnpj CNPJ document number as plain text
     * @param size company size classification
     * @param address embedded address of the company
     */
    public CompanyJpaEntity(UUID id, String legalName, String tradeName, String cnpj, CompanySize size,
            AddressEmbeddable address) {
        super(id);
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.size = size;
        this.address = address;
    }

    /**
     * Returns the registered legal name.
     *
     * @return the legal name
     */
    public String getLegalName() {
        return legalName;
    }

    /**
     * Returns the trade (fantasy) name.
     *
     * @return the trade name
     */
    public String getTradeName() {
        return tradeName;
    }

    /**
     * Returns the CNPJ document number.
     *
     * @return the CNPJ as plain text
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * Returns the company size classification.
     *
     * @return the company size
     */
    public CompanySize getSize() {
        return size;
    }

    /**
     * Returns the embedded address.
     *
     * @return the address
     */
    public AddressEmbeddable getAddress() {
        return address;
    }

    /**
     * Indicates whether the company is active.
     *
     * @return {@code true} if active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the company.
     *
     * @param active {@code true} to mark the company active, {@code false} otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Embeddable JPA representation of a postal address, mapped as part of
     * {@link CompanyJpaEntity}.
     */
    @Embeddable
    public static class AddressEmbeddable {

        private String street;
        private String number;
        private String city;
        private String state;
        private String zipCode;
        private String country;

        /**
         * Default constructor required by JPA.
         */
        protected AddressEmbeddable() {
        }

        /**
         * Creates a new embeddable address.
         *
         * @param street street name
         * @param number street number
         * @param city city name
         * @param state state or federative unit (UF)
         * @param zipCode postal code (CEP)
         * @param country country name
         */
        public AddressEmbeddable(String street, String number, String city, String state, String zipCode,
                String country) {
            this.street = street;
            this.number = number;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.country = country;
        }

        /**
         * Returns the street name.
         *
         * @return the street
         */
        public String getStreet() {
            return street;
        }

        /**
         * Returns the street number.
         *
         * @return the number
         */
        public String getNumber() {
            return number;
        }

        /**
         * Returns the city name.
         *
         * @return the city
         */
        public String getCity() {
            return city;
        }

        /**
         * Returns the state or federative unit (UF).
         *
         * @return the state
         */
        public String getState() {
            return state;
        }

        /**
         * Returns the postal code (CEP).
         *
         * @return the zip code
         */
        public String getZipCode() {
            return zipCode;
        }

        /**
         * Returns the country name.
         *
         * @return the country
         */
        public String getCountry() {
            return country;
        }
    }
}
