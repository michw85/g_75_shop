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
 * Глобальный обработчик исключений для всех контроллеров
 * Перехватывает исключения и возвращает понятные сообщения клиенту
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обработка исключения EntityNotFoundException (сущность не найдена)
     * Возвращает 404 NOT FOUND с сообщением об ошибке
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        String message = e.getMessage();
        logger.warn("Entity not found: {}", message);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка исключения FileUploadException (ошибка при обновлении сущности)
     * Возвращает 400 BAD REQUEST с сообщением об ошибке
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleException(FileUploadException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключения EntityUpdateException (ошибка при обновлении сущности)
     * Возвращает 400 BAD REQUEST с сообщением об ошибке
     * Логируется на уровне WARN как бизнес-ошибка
     */
    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<String> handleEntityUpdate(EntityUpdateException e) {
        String message = e.getMessage();
        logger.warn("Entity update error: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка ConstraintViolationException (ошибки валидации на уровне параметров методов)
     * Возвращает 400 BAD REQUEST со списком сообщений об ошибках
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
     * Обработка MethodArgumentNotValidException (ошибки валидации @Valid в теле запроса)
     * Возвращает 400 BAD REQUEST со списком сообщений об ошибках
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
     * Обработка NullPointerException (попытка использовать null)
     * Возвращает 400 BAD REQUEST (так как это ошибка клиента)
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
     * Обработка MethodArgumentTypeMismatchException (неверный тип параметра)
     * Например, передача строки вместо числа в ID
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("Parameter '%s' should be of type %s",
                e.getName(), e.getRequiredType().getSimpleName());
        logger.warn("Type mismatch: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка HttpMessageNotReadableException (некорректный JSON)
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
     * Обработка всех остальных исключений (непредвиденные ошибки)
     * Возвращает 500 INTERNAL SERVER ERROR
     * Логируется на уровне ERROR
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
     * Обработка исключения AuthorizationException (ошибка при авторизации)
     * Возвращает UNAUTHORIZED с сообщением об ошибке
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
     * Обработка исключения RegistrationException (ошибка при регистрации)
     * Возвращает 400 BAD_REQUEST с сообщением об ошибке
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<String> handleException(RegistrationException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

}
