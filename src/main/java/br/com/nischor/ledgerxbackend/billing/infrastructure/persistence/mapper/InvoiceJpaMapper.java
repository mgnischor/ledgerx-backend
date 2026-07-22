package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.InstallmentJpaEntity;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.InvoiceJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class InvoiceJpaMapper {

    public Invoice toDomain(InvoiceJpaEntity entity) {
        var installments = entity.getInstallments().stream()
                .map(installment -> {
                    var domainInstallment = new Installment(installment.getId(), installment.getNumber(),
                            new Money(installment.getAmount(), Currency.getInstance("BRL")),
                            installment.getDueDate());
                    if (installment.isPaid()) {
                        domainInstallment.markAsPaid(installment.getPaidOn());
                    }
                    return domainInstallment;
                })
                .toList();

        return new Invoice(entity.getId(), entity.getCompanyId(), entity.getPartyId(), entity.getDirection(),
                installments);
    }

    public InvoiceJpaEntity toEntity(Invoice invoice) {
        var entity = new InvoiceJpaEntity(invoice.getCompanyId(), invoice.getPartyId(), invoice.getDirection());
        entity.setStatus(invoice.getStatus());
        invoice.getInstallments().forEach(installment -> {
            var installmentEntity = new InstallmentJpaEntity(entity, installment.getNumber(),
                    installment.getAmount().amount(), installment.getDueDate());
            if (installment.isPaid()) {
                installmentEntity.markAsPaid(installment.getPaidOn());
            }
            entity.getInstallments().add(installmentEntity);
        });
        return entity;
    }
}
