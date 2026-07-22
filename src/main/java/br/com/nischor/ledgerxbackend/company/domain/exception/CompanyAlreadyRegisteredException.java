package br.com.nischor.ledgerxbackend.company.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

public class CompanyAlreadyRegisteredException extends BusinessRuleViolationException {

    public CompanyAlreadyRegisteredException(String cnpj) {
        super("Company already registered for CNPJ: %s".formatted(cnpj));
    }
}
