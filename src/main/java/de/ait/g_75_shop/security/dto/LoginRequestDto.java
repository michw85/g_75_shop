package de.ait.g_75_shop.security.dto;

/**
 * DTO for login request
 * Contains user credentials for authentication
 *
 * DTO для запроса входа в систему
 * Содержит учетные данные пользователя для аутентификации
 */
public class LoginRequestDto {

    private String email;
    private String password;

    /**
     * Default constructor
     * Конструктор по умолчанию
     */
    public LoginRequestDto() {
    }

    // Getters and setters / Геттеры и сеттеры
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("Login request DTO: email - %s", email);
    }
}
