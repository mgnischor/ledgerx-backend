package br.com.nischor.ledgerxbackend.billing.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Invoice {

    private final UUID id;
    private final UUID companyId;
    private final UUID partyId;
    private final PartyType direction;
    private final List<Installment> installments;
    private InvoiceStatus status;

    public Invoice(UUID id, UUID companyId, UUID partyId, PartyType direction, List<Installment> installments) {
        this.id = id;
        this.companyId = companyId;
        this.partyId = partyId;
        this.direction = direction;
        this.installments = new ArrayList<>(installments);
        this.status = InvoiceStatus.OPEN;
    }

    public void registerPayment(UUID installmentId, LocalDate paidOn) {
        if (status == InvoiceStatus.CANCELED) {
            throw new BusinessRuleViolationException("Cannot register a payment for a canceled invoice");
        }

        installments.stream()
                .filter(installment -> installment.getId().equals(installmentId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Installment %s does not belong to this invoice".formatted(installmentId)))
                .markAsPaid(paidOn);

        this.status = installments.stream().allMatch(Installment::isPaid)
                ? InvoiceStatus.PAID
                : InvoiceStatus.PARTIALLY_PAID;
    }

    public void cancel() {
        if (status == InvoiceStatus.PAID) {
            throw new BusinessRuleViolationException("A fully paid invoice cannot be canceled");
        }
        this.status = InvoiceStatus.CANCELED;
    }

    public void markOverdueIfNeeded(LocalDate referenceDate) {
        if (status == InvoiceStatus.OPEN && installments.stream().anyMatch(i -> i.isOverdue(referenceDate))) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public PartyType getDirection() {
        return direction;
    }

    public List<Installment> getInstallments() {
        return List.copyOf(installments);
    }

    public InvoiceStatus getStatus() {
        return status;
    }
}
