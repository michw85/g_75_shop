package de.ait.g_75_shop.exceptions.types;
/**
 * Исключение, выбрасываемое при ошибках обновления сущностей
 * Например, при попытке добавить товар в корзину, когда товар неактивен
 * или при попытке удалить товар из пустой корзины
 */
public class EntityUpdateException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке
     * @param message детальное описание ошибки
     */
    public EntityUpdateException(String message) {
        super(message);
    }

    /**
     * Конструктор с сообщением об ошибке и причиной
     * @param message детальное описание ошибки
     * @param cause причина исключения
     */
    public EntityUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
