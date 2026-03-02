package de.ait.g_75_shop.exceptions.types;
/**
 * Exception thrown when entity update operations fail
 * Examples: adding product to cart when product is inactive,
 * removing product from empty cart, etc.
 *
 * Исключение, выбрасываемое при ошибках обновления сущностей
 * Например: при попытке добавить товар в корзину, когда товар неактивен,
 * или при попытке удалить товар из пустой корзины
 */
public class EntityUpdateException extends RuntimeException {
    /**
     * Constructor with error message
     * Конструктор с сообщением об ошибке
     *
     * @param message detailed error description / детальное описание ошибки
     */
    public EntityUpdateException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause
     * Конструктор с сообщением об ошибке и причиной
     *
     * @param message detailed error description / детальное описание ошибки
     * @param cause the cause of the exception / причина исключения
     */
    public EntityUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
