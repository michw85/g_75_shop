package de.ait.g_75_shop.exceptions.types;

/**
 * Exception thrown when email sending fails during user registration
 * Wraps the underlying cause of email sending failure
 *
 * Исключение, выбрасываемое при ошибках отправки email во время регистрации пользователя
 * Обертывает основную причину сбоя отправки email
 */
public class EmailSendingException extends RuntimeException {

    /**
     * Constructor with error message and cause
     * Конструктор с сообщением об ошибке и причиной
     *
     * @param message detailed error description / детальное описание ошибки
     * @param cause the cause of the exception / причина исключения
     */
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
