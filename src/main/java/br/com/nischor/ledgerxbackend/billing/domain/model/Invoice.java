package br.com.nischor.ledgerxbackend.billing.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root representing an invoice raised against a party, composed of one or more {@link Installment
 * installments}. Enforces the invoice lifecycle rules: payments cannot be registered on a canceled invoice, a fully
 * paid invoice cannot be canceled, and the status transitions automatically as installments are paid or become
 * overdue.
 */
public class Invoice {

    private final UUID id;
    private final UUID companyId;
    private final UUID partyId;
    private final PartyType direction;
    private final List<Installment> installments;
    private InvoiceStatus status;

    /**
     * Creates a new invoice in {@link InvoiceStatus#OPEN} status.
     *
     * @param id           the invoice identifier.
     * @param companyId    the identifier of the company the invoice belongs to.
     * @param partyId      the identifier of the counterparty of the invoice.
     * @param direction    whether the invoice is issued to a customer or received from a supplier.
     * @param installments the installments composing the invoice; copied defensively.
     */
    public Invoice(UUID id, UUID companyId, UUID partyId, PartyType direction, List<Installment> installments) {
        this.id = id;
        this.companyId = companyId;
        this.partyId = partyId;
        this.direction = direction;
        this.installments = new ArrayList<>(installments);
        this.status = InvoiceStatus.OPEN;
    }

    /**
     * Registers the payment of one of this invoice's installments and updates the invoice status accordingly: the
     * invoice becomes {@link InvoiceStatus#PAID} if all installments are now paid, or
     * {@link InvoiceStatus#PARTIALLY_PAID} otherwise.
     *
     * @param installmentId the identifier of the installment being paid.
     * @param paidOn         the date the payment was made.
     * @throws BusinessRuleViolationException if the invoice is canceled, or if no installment with the given
     *                                        identifier belongs to this invoice.
     */
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

    /**
     * Cancels this invoice.
     *
     * @throws BusinessRuleViolationException if the invoice is already fully paid.
     */
    public void cancel() {
        if (status == InvoiceStatus.PAID) {
            throw new BusinessRuleViolationException("A fully paid invoice cannot be canceled");
        }
        this.status = InvoiceStatus.CANCELED;
    }

    /**
     * Transitions this invoice to {@link InvoiceStatus#OVERDUE} if it is currently {@link InvoiceStatus#OPEN} and at
     * least one installment is overdue as of the given reference date. Has no effect otherwise.
     *
     * @param referenceDate the date used to evaluate whether installments are overdue.
     */
    public void markOverdueIfNeeded(LocalDate referenceDate) {
        if (status == InvoiceStatus.OPEN && installments.stream().anyMatch(i -> i.isOverdue(referenceDate))) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    /**
     * @return the invoice identifier.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the identifier of the company the invoice belongs to.
     */
    public UUID getCompanyId() {
        return companyId;
    }

    /**
     * @return the identifier of the counterparty of the invoice.
     */
    public UUID getPartyId() {
        return partyId;
    }

    /**
     * @return whether the invoice is issued to a customer or received from a supplier.
     */
    public PartyType getDirection() {
        return direction;
    }

    /**
     * @return an immutable copy of the installments composing this invoice.
     */
    public List<Installment> getInstallments() {
        return List.copyOf(installments);
    }

    /**
     * @return the current lifecycle status of the invoice.
     */
    public InvoiceStatus getStatus() {
        return status;
    }
}
