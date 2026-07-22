package br.com.nischor.ledgerxbackend.billing.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.LocalDate;
import java.util.UUID;

public class Installment {

    private final UUID id;
    private final int number;
    private final Money amount;
    private final LocalDate dueDate;
    private boolean paid;
    private LocalDate paidOn;

    public Installment(UUID id, int number, Money amount, LocalDate dueDate) {
        this.id = id;
        this.number = number;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paid = false;
    }

    public void markAsPaid(LocalDate paidOn) {
        this.paid = true;
        this.paidOn = paidOn;
    }

    public boolean isOverdue(LocalDate referenceDate) {
        return !paid && dueDate.isBefore(referenceDate);
    }

    public UUID getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public Money getAmount() {
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
}
