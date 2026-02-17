package de.ait.g_75_shop.exceptions.types;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityType, Long id) {
        super(String.format("%s with id %d not found", entityType.getSimpleName(), id));
    }
}
