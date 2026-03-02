package de.ait.g_75_shop.exceptions.types;

/**
 * Exception thrown when authorization fails
 * Used for authentication and permission issues
 *
 * Исключение, выбрасываемое при ошибках авторизации
 * Используется для проблем аутентификации и прав доступа
 */
public class AuthorizationException extends RuntimeException {
    /**
     * Constructor with error message
     * Конструктор с сообщением об ошибке
     *
     * @param message detailed error description / детальное описание ошибки
     */
    public AuthorizationException(String message) {
        super(message);
    }
}
