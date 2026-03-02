package de.ait.g_75_shop.exceptions.types;

/**
 * Exception thrown when user registration fails
 * Used for cases like email already registered, validation errors, etc.
 * <p>
 * Исключение, выбрасываемое при ошибках регистрации пользователя
 * Используется в случаях, когда email уже зарегистрирован, ошибки валидации и т.д.
 */
public class RegistrationException extends RuntimeException {
    /**
     * Constructor with error message
     * Конструктор с сообщением об ошибке
     *
     * @param message detailed error description / детальное описание ошибки
     */
    public RegistrationException(String message) {
        super(message);
    }
}
