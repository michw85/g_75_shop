package de.ait.g_75_shop.exceptions.types;

/**
 * Exception thrown when an entity is not found in the database
 * Used when searching by ID or other criteria
 *
 * Исключение, выбрасываемое когда сущность не найдена в базе данных
 * Используется при поиске по ID или другим критериям
 */
public class EntityNotFoundException extends RuntimeException {
    /**
     * Constructor with entity type and ID
     * Конструктор с типом сущности и ID
     *
     * @param entityType class of the entity / класс сущности
     * @param id identifier that was searched / идентификатор, по которому искали
     */
    public EntityNotFoundException(Class<?> entityType, Long id) {
        super(String.format("%s with id %d not found", entityType.getSimpleName(), id));
    }
}
