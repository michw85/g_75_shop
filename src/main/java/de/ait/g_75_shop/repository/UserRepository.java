package de.ait.g_75_shop.repository;

import de.ait.g_75_shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entity operations
 * Provides methods for user authentication and management
 *
 * Интерфейс репозитория для операций с сущностью User
 * Предоставляет методы для аутентификации и управления пользователями
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds user by email address
     * Email is used as username for authentication
     *
     * Находит пользователя по адресу электронной почты
     * Email используется как имя пользователя для аутентификации
     *
     * @param email user's email address / адрес электронной почты пользователя
     * @return Optional containing user if found / Optional с пользователем, если найден
     */
    Optional<User> findByEmail(String email);
}
