package br.com.nischor.ledgerxbackend.accounting.domain.model;

import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.time.YearMonth;
import java.util.UUID;

public class Budget {

    private final UUID id;
    private final UUID companyId;
    private final UUID categoryId;
    private final YearMonth period;
    private Money limit;
    private boolean active;

    public Budget(UUID id, UUID companyId, UUID categoryId, YearMonth period, Money limit) {
        this.id = id;
        this.companyId = companyId;
        this.categoryId = categoryId;
        this.period = period;
        this.limit = limit;
        this.active = true;
    }

    public void revise(Money limit) {
        this.limit = limit;
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public Money getLimit() {
        return limit;
    }

    public boolean isActive() {
        return active;
    }
}
