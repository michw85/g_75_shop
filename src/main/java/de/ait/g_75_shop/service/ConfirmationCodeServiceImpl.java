package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.ConfirmationCode;
import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.repository.ConfirmationCodeRepository;
import de.ait.g_75_shop.service.interfaces.ConfirmationCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of ConfirmationCodeService interface
 * Handles generation and management of email confirmation codes
 *
 * Реализация интерфейса ConfirmationCodeService
 * Обрабатывает генерацию и управление кодами подтверждения email
 */
@Service
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private final ConfirmationCodeRepository repository;

    /**
     * Constructor with dependency injection
     * Конструктор с внедрением зависимости
     *
     * @param repository repository for confirmation code operations / репозиторий для операций с кодами подтверждения
     */
    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    /**
     * Generates a unique confirmation code for user
     * Code is valid for 24 hours from creation
     *
     * Генерирует уникальный код подтверждения для пользователя
     * Код действителен в течение 24 часов с момента создания
     *
     * @param user user to generate code for / пользователь, для которого генерируется код
     * @return generated confirmation code value / сгенерированное значение кода подтверждения
     */
    @Override
    public String generateConfirmationCode(User user) {
        // Generate random UUID as confirmation code / Генерируем случайный UUID в качестве кода подтверждения
        String value = UUID.randomUUID().toString();
        // Set expiration to 24 hours from now / Устанавливаем срок действия - 24 часа от текущего момента
        LocalDateTime expiration = LocalDateTime.now().plusHours(24);
        // Create and save confirmation code entity / Создаем и сохраняем сущность кода подтверждения
        ConfirmationCode entity = new ConfirmationCode(value, expiration,user);
        repository.save(entity);
        return value;
    }
}
