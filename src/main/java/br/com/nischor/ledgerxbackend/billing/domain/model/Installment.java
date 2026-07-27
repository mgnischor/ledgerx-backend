package br.com.nischor.ledgerxbackend.billing.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a single installment of an {@link Invoice}, tracking its due date and payment state.
 */
public class Installment {

    private final UUID id;
    private final int number;
    private final Money amount;
    private final LocalDate dueDate;
    private boolean paid;
    private LocalDate paidOn;

    /**
     * Creates a new, unpaid installment.
     *
     * @param id      the installment identifier.
     * @param number  the 1-based sequence number of the installment within its invoice.
     * @param amount  the amount due for this installment.
     * @param dueDate the date by which the installment must be paid.
     */
    public Installment(UUID id, int number, Money amount, LocalDate dueDate) {
        this.id = id;
        this.number = number;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paid = false;
    }

    /**
     * Marks this installment as paid.
     *
     * @param paidOn the date the payment was made.
     */
    public void markAsPaid(LocalDate paidOn) {
        this.paid = true;
        this.paidOn = paidOn;
    }

    /**
     * Determines whether this installment is overdue as of the given reference date.
     *
     * @param referenceDate the date to compare the due date against.
     * @return {@code true} if the installment is unpaid and its due date is before {@code referenceDate}.
     */
    public boolean isOverdue(LocalDate referenceDate) {
        return !paid && dueDate.isBefore(referenceDate);
    }

    /**
     * @return the installment identifier.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the 1-based sequence number of the installment within its invoice.
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the amount due for this installment.
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * @return the date by which the installment must be paid.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * @return {@code true} if the installment has been paid.
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * @return the date the installment was paid, or {@code null} if it has not been paid yet.
     */
    public LocalDate getPaidOn() {
        return paidOn;
    }
}
