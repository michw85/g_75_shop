package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.dto.user.UserRegistrationDto;
import de.ait.g_75_shop.service.interfaces.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user management
 * Handles user registration and authentication endpoints
 *
 * REST контроллер для управления пользователями
 * Обрабатывает endpoints регистрации и аутентификации пользователей
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Registers a new user
     * POST /users/register
     *
     * Регистрирует нового пользователя
     *
     * @param registrationDto user registration data / данные регистрации пользователя
     * @return confirmation message with email instructions / сообщение с инструкциями по email
     */

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto registrationDto) {
        // обращение к сервису
        service.register(registrationDto);
        return "Registration complete. Please check your email.";
    }
}
