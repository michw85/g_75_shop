package de.ait.g_75_shop.exceptions;

import de.ait.g_75_shop.exceptions.types.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all controllers
 * Intercepts exceptions and returns user-friendly messages to clients
 *
 * Глобальный обработчик исключений для всех контроллеров
 * Перехватывает исключения и возвращает понятные сообщения клиенту
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles EntityNotFoundException (entity not found)
     * Returns 404 NOT FOUND with error message
     *
     * Обработка исключения EntityNotFoundException (сущность не найдена)
     * Возвращает 404 NOT FOUND с сообщением об ошибке
     *
     * @param e the exception / исключение
     * @return response with 404 status and message / ответ со статусом 404 и сообщением
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        String message = e.getMessage();
        logger.warn("Entity not found: {}", message);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles FileUploadException (error during file upload)
     * Returns 400 BAD REQUEST with error message
     *
     * Обработка исключения FileUploadException (ошибка при загрузке файла)
     * Возвращает 400 BAD REQUEST с сообщением об ошибке
     *
     * @param e the exception / исключение
     * @return response with 400 status and message / ответ со статусом 400 и сообщением
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleException(FileUploadException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles EntityUpdateException (error during entity update)
     * Returns 400 BAD REQUEST with error message
     * Logged at WARN level as business error
     *
     * Обработка исключения EntityUpdateException (ошибка при обновлении сущности)
     * Возвращает 400 BAD REQUEST с сообщением об ошибке
     * Логируется на уровне WARN как бизнес-ошибка
     *
     * @param e the exception / исключение
     * @return response with 400 status and message / ответ со статусом 400 и сообщением
     */
    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<String> handleEntityUpdate(EntityUpdateException e) {
        String message = e.getMessage();
        logger.warn("Entity update error: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException (validation errors at method parameter level)
     * Returns 400 BAD REQUEST with list of error messages
     *
     * Обработка ConstraintViolationException (ошибки валидации на уровне параметров методов)
     * Возвращает 400 BAD REQUEST со списком сообщений об ошибках
     *
     * @param e the exception / исключение
     * @return response with 400 status and error list / ответ со статусом 400 и списком ошибок
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleConstraintViolation(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        messages.forEach(msg -> logger.warn("Validation error: {}", msg));
        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles MethodArgumentNotValidException (@Valid validation errors in request body)
     * Returns 400 BAD REQUEST with list of field-specific error messages
     *
     * Обработка MethodArgumentNotValidException (ошибки валидации @Valid в теле запроса)
     * Возвращает 400 BAD REQUEST со списком сообщений об ошибках по полям
     *
     * @param e the exception / исключение
     * @return response with 400 status and field error list / ответ со статусом 400 и списком ошибок полей
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> messages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        messages.forEach(msg -> logger.warn("Validation error in request body: {}", msg));
        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles NullPointerException (attempt to use null)
     * Returns 500 INTERNAL SERVER ERROR as this is typically a server-side bug
     *
     * Обработка NullPointerException (попытка использовать null)
     * Возвращает 500 INTERNAL SERVER ERROR, так как это обычно ошибка на стороне сервера
     *
     * @param e the exception / исключение
     * @return response with 500 status / ответ со статусом 500
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleException(NullPointerException e) {
        String message = e.getMessage();
        logger.error(message, e);
        return new ResponseEntity<>(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles IOException (input/output errors)
     * Returns 500 INTERNAL SERVER ERROR
     *
     * Обработка IOException (ошибки ввода/вывода)
     * Возвращает 500 INTERNAL SERVER ERROR
     *
     * @param e the exception / исключение
     * @return response with 500 status / ответ со статусом 500
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleException(IOException e) {
        String message = e.getMessage();
        logger.error(message, e);
        return new ResponseEntity<>(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles MethodArgumentTypeMismatchException (invalid parameter type)
     * For example, passing string instead of number for ID
     * Returns 400 BAD REQUEST
     *
     * Обработка MethodArgumentTypeMismatchException (неверный тип параметра)
     * Например, передача строки вместо числа в ID
     * Возвращает 400 BAD REQUEST
     *
     * @param e the exception / исключение
     * @return response with 400 status and message / ответ со статусом 400 и сообщением
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("Parameter '%s' should be of type %s",
                e.getName(), e.getRequiredType().getSimpleName());
        logger.warn("Type mismatch: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException (malformed JSON)
     * Returns 400 BAD REQUEST
     *
     * Обработка HttpMessageNotReadableException (некорректный JSON)
     * Возвращает 400 BAD REQUEST
     *
     * @param e the exception / исключение
     * @return response with 400 status and message / ответ со статусом 400 и сообщением
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        logger.warn("Malformed JSON request: {}", e.getMessage());
        return new ResponseEntity<>(
                "Malformed JSON request. Please check your request body format.",
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles all other exceptions (unexpected errors)
     * Returns 500 INTERNAL SERVER ERROR
     * Logged at ERROR level
     *
     * Обработка всех остальных исключений (непредвиденные ошибки)
     * Возвращает 500 INTERNAL SERVER ERROR
     * Логируется на уровне ERROR
     *
     * @param e the exception / исключение
     * @return generic error message / общее сообщение об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles AuthorizationException (authentication/authorization errors)
     * Returns 401 UNAUTHORIZED
     *
     * Обработка исключения AuthorizationException (ошибка при авторизации)
     * Возвращает UNAUTHORIZED с сообщением об ошибке
     *
     * @param e the exception / исключение
     * @return response with 401 status and message / ответ со статусом 401 и сообщением
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<String> handleException(AuthorizationException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(
                message,
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
            * Handles RegistrationException (errors during user registration)
     * Returns 400 BAD_REQUEST
     *
             * Обработка исключения RegistrationException (ошибка при регистрации)
     * Возвращает 400 BAD_REQUEST с сообщением об ошибке
     *
             * @param e the exception / исключение
     * @return response with 400 status and message / ответ со статусом 400 и сообщением
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<String> handleException(RegistrationException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles EmailSendingException (errors during user-registration email)
     * Returns 500 INTERNAL SERVER ERROR
     * Logged at ERROR level
     *
     * Обработка исключения EmailSendingException (ошибка при отправке письма-подтверждения регистрации пользователю)
     * Возвращает 500 INTERNAL SERVER ERROR
     * Логируется на уровне ERROR
     *
     * @param e the exception / исключение
     * @return response with 500 status and status-message / ответ со статусом 500 и status-сообщением. Логгируем сообщение только для разработчика
     */
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<String> handleException(EmailSendingException e) {
        String message = e.getMessage();
        logger.error(message, e);
        return new ResponseEntity<>(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
