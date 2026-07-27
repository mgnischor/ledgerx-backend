package br.com.nischor.ledgerxbackend.shared.domain.exception;

/**
 * Thrown when an entity of a given type cannot be found for a given identifier.
 */
public class EntityNotFoundException extends DomainException {

    /**
     * Creates a new exception describing the missing entity.
     *
     * @param entityType the class of the entity that could not be found
     * @param id         the identifier that was used to look up the entity
     */
    public EntityNotFoundException(Class<?> entityType, Object id) {
        super("%s not found for id %s".formatted(entityType.getSimpleName(), id));
    }
}
