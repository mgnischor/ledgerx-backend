package br.com.nischor.ledgerxbackend.billing.infrastructure.persistence.entity;

import br.com.nischor.ledgerxbackend.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "installments")
public class InstallmentJpaEntity extends BaseEntity {

    @ManyToOne
    private InvoiceJpaEntity invoice;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean paid = false;

    private LocalDate paidOn;

    protected InstallmentJpaEntity() {
    }

    public InstallmentJpaEntity(UUID id, InvoiceJpaEntity invoice, int number, BigDecimal amount,
            LocalDate dueDate) {
        super(id);
        this.invoice = invoice;
        this.number = number;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public InvoiceJpaEntity getInvoice() {
        return invoice;
    }

    public int getNumber() {
        return number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public LocalDate getPaidOn() {
        return paidOn;
    }

    public void markAsPaid(LocalDate paidOn) {
        this.paid = true;
        this.paidOn = paidOn;
    }
}
