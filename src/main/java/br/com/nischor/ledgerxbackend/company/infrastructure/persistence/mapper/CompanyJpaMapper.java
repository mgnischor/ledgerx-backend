package br.com.nischor.ledgerxbackend.company.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.company.infrastructure.persistence.entity.CompanyJpaEntity;
import br.com.nischor.ledgerxbackend.company.infrastructure.persistence.entity.CompanyJpaEntity.AddressEmbeddable;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import org.springframework.stereotype.Component;

@Component
public class CompanyJpaMapper {

    public Company toDomain(CompanyJpaEntity entity) {
        var address = new Address(entity.getAddress().getStreet(), entity.getAddress().getNumber(),
                entity.getAddress().getCity(), entity.getAddress().getState(), entity.getAddress().getZipCode(),
                entity.getAddress().getCountry());
        return new Company(entity.getId(), entity.getLegalName(), entity.getTradeName(),
                DocumentNumber.cnpj(entity.getCnpj()), entity.getSize(), address);
    }

    public CompanyJpaEntity toEntity(Company company) {
        var address = company.getAddress();
        var embeddable = new AddressEmbeddable(address.street(), address.number(), address.city(), address.state(),
                address.zipCode(), address.country());
        return new CompanyJpaEntity(company.getLegalName(), company.getTradeName(), company.getCnpj().value(),
                company.getSize(), embeddable);
    }
}
