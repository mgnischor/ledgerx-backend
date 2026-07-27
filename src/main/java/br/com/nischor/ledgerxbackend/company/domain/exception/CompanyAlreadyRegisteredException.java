package br.com.nischor.ledgerxbackend.company.domain.exception;

import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;

/**
 * Thrown when attempting to register a company whose CNPJ is already registered.
 */
public class CompanyAlreadyRegisteredException extends BusinessRuleViolationException {

    /**
     * Creates the exception with a message identifying the conflicting CNPJ.
     *
     * @param cnpj the CNPJ that is already registered
     */
    public CompanyAlreadyRegisteredException(String cnpj) {
        super("Company already registered for CNPJ: %s".formatted(cnpj));
    }
}
