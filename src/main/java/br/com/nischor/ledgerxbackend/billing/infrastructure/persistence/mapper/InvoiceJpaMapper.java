package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.mapper;

import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.InstallmentJpaEntity;
import br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity.InvoiceJpaEntity;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.util.Currency;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link Invoice} domain aggregate and its {@link InvoiceJpaEntity}
 * persistence representation, including the nested installments.
 */
@Component
public class InvoiceJpaMapper {

    /**
     * Converts a JPA invoice entity, together with its installments, into a domain invoice.
     *
     * @param entity the JPA invoice entity to convert
     * @return the corresponding domain invoice, with installments and payment status restored
     */
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

    /**
     * Converts a domain invoice, together with its installments, into a JPA entity ready for
     * persistence.
     *
     * @param invoice the domain invoice to convert
     * @return the corresponding JPA invoice entity, with installment entities and payment status set
     */
    public InvoiceJpaEntity toEntity(Invoice invoice) {
        var entity = new InvoiceJpaEntity(invoice.getId(), invoice.getCompanyId(), invoice.getPartyId(),
                invoice.getDirection());
        entity.setStatus(invoice.getStatus());
        invoice.getInstallments().forEach(installment -> {
            var installmentEntity = new InstallmentJpaEntity(installment.getId(), entity, installment.getNumber(),
                    installment.getAmount().amount(), installment.getDueDate());
            if (installment.isPaid()) {
                installmentEntity.markAsPaid(installment.getPaidOn());
            }
            entity.getInstallments().add(installmentEntity);
        });
        return entity;
    }
}
