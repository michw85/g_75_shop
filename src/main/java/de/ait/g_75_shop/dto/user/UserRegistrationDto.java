package de.ait.g_75_shop.dto.user;

/**
 * DTO for user registration
 * Contains all necessary information for creating a new user account
 *
 * DTO для регистрации пользователя
 * Содержит всю необходимую информацию для создания новой учетной записи
 */
public class UserRegistrationDto {

    private String email;
    private String password;
    private String name;

    public UserRegistrationDto() {
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format( "User Registration Dto: email %s, name - %s", email, name);
    }
}
