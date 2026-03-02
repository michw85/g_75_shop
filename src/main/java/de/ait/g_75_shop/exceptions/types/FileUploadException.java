package de.ait.g_75_shop.exceptions.types;

/**
 * Exception thrown when file upload operations fail
 * Used for various file-related errors (empty file, wrong type, etc.)
 *
 * Исключение, выбрасываемое при ошибках загрузки файлов
 * Используется для различных ошибок, связанных с файлами (пустой файл, неправильный тип и т.д.)
 */
public class FileUploadException extends RuntimeException {
    /**
     * Constructor with error message
     * Конструктор с сообщением об ошибке
     *
     * @param message detailed error description / детальное описание ошибки
     */
    public FileUploadException(String message) {
        super(message);
    }
}
