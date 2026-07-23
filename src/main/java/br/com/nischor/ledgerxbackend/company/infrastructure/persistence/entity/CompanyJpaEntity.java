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

@Entity
@Table(name = "companies")
public class CompanyJpaEntity extends AuditableEntity {

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false)
    private String tradeName;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanySize size;

    @Embedded
    private AddressEmbeddable address;

    @Column(nullable = false)
    private boolean active = true;

    protected CompanyJpaEntity() {
    }

    public CompanyJpaEntity(UUID id, String legalName, String tradeName, String cnpj, CompanySize size,
            AddressEmbeddable address) {
        super(id);
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.size = size;
        this.address = address;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getCnpj() {
        return cnpj;
    }

    public CompanySize getSize() {
        return size;
    }

    public AddressEmbeddable getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }

    @Embeddable
    public static class AddressEmbeddable {

        private String street;
        private String number;
        private String city;
        private String state;
        private String zipCode;
        private String country;

        protected AddressEmbeddable() {
        }

        public AddressEmbeddable(String street, String number, String city, String state, String zipCode,
                String country) {
            this.street = street;
            this.number = number;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.country = country;
        }

        public String getStreet() {
            return street;
        }

        public String getNumber() {
            return number;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCountry() {
            return country;
        }
    }
}
