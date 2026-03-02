package de.ait.g_75_shop.domain;

import de.ait.g_75_shop.domain.enums.Role;
import jakarta.persistence.*;


import java.util.Objects;

/**
 * User entity for authentication and authorization
 * Represents system users with different roles
 * <p>
 * Сущность пользователя для аутентификации и авторизации
 * Представляет пользователей системы с разными ролями
 */
@Entity
// SELECT * FROM user - Postgres такой запрос не поймёт. user - уже зарезервирован им. только если `user`
//@Table(name = "user")
@Table(name = "account")
// 'user' is a reserved keyword in PostgreSQL, using 'account' instead / зарезервированное слово в PostgreSQL, используем 'account'
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * User's email address (used as username)
     * Адрес электронной почты пользователя (используется как имя пользователя)
     */
    @Column(name = "email")
    private String email;

    /**
     * Encrypted password
     * Зашифрованный пароль
     */
    @Column(name = "password")
    private String password;

    /**
     * User's display name
     * Отображаемое имя пользователя
     */
    @Column(name = "name")
    private String name;

    /**
     * Email confirmation status
     * If false, user cannot log in
     *
     * Статус подтверждения email
     * Если false, пользователь не может войти
     */
    @Column(name = "confirmed")
    private boolean confirmed;

    /**
     * User's role (ADMIN or USER)
     * Stored as string in database
     *
     * Роль пользователя (ADMIN или USER)
     * Хранится как строка в базе данных
     */
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    // Getters and setters / Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // equals, hashCode, toString methods / методы equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof User user)) {
            return false;
        }

        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("User: id - %d, email - %s, name - %s, confirmed - %s, role - %s",
                id, email, name, confirmed ? "yes" : "no", role == null ? "none" : role);
    }
}
