package br.com.nischor.ledgerxbackend.shared.domain.exception;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(Class<?> entityType, Object id) {
        super("%s not found for id %s".formatted(entityType.getSimpleName(), id));
    }
}
