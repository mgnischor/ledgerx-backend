package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity mapping a single installment of an {@link InvoiceJpaEntity} to the {@code
 * installments} table, including its due amount, due date and payment status.
 */
@Entity
@Table(name = "installments")
public class InstallmentJpaEntity extends BaseEntity {

    /** Invoice this installment belongs to. */
    @ManyToOne
    private InvoiceJpaEntity invoice;

    /** Sequential number of this installment within the invoice. */
    @Column(nullable = false)
    private int number;

    /** Amount due for this installment. */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /** Date on which this installment is due. */
    @Column(nullable = false)
    private LocalDate dueDate;

    /** Whether this installment has been paid. */
    @Column(nullable = false)
    private boolean paid = false;

    /** Date on which this installment was actually paid, if any. */
    private LocalDate paidOn;

    /** JPA-only default constructor. */
    protected InstallmentJpaEntity() {
    }

    /**
     * Creates a new installment.
     *
     * @param id the installment identifier
     * @param invoice the invoice this installment belongs to
     * @param number the sequential number of the installment within the invoice
     * @param amount the amount due for the installment
     * @param dueDate the date on which the installment is due
     */
    public InstallmentJpaEntity(UUID id, InvoiceJpaEntity invoice, int number, BigDecimal amount,
            LocalDate dueDate) {
        super(id);
        this.invoice = invoice;
        this.number = number;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    /**
     * Returns the invoice this installment belongs to.
     *
     * @return the parent invoice entity
     */
    public InvoiceJpaEntity getInvoice() {
        return invoice;
    }

    /**
     * Returns the sequential number of this installment within the invoice.
     *
     * @return the installment number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the amount due for this installment.
     *
     * @return the installment amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the date on which this installment is due.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns whether this installment has been paid.
     *
     * @return {@code true} if the installment is paid, {@code false} otherwise
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * Returns the date on which this installment was paid.
     *
     * @return the payment date, or {@code null} if not yet paid
     */
    public LocalDate getPaidOn() {
        return paidOn;
    }

    /**
     * Marks this installment as paid on the given date.
     *
     * @param paidOn the date the payment was made
     */
    public void markAsPaid(LocalDate paidOn) {
        this.paid = true;
        this.paidOn = paidOn;
    }
}
