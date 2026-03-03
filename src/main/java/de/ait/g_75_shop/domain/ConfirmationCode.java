package de.ait.g_75_shop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity for email confirmation codes
 * Used during user registration to verify email address
 *
 * Сущность для кодов подтверждения email
 * Используется при регистрации пользователя для подтверждения email адреса
 */
@Entity
@Table(name = "confirmation_code")
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Confirmation code value (usually random string)
     * Значение кода подтверждения (обычно случайная строка)
     */
    @Column(name = "value")
    private String value;

    /**
     * Expiration date/time of the confirmation code
     * Дата/время истечения срока действия кода подтверждения
     */
    @Column(name = "expiration")
    private LocalDateTime expiration;

    /**
     * User associated with this confirmation code
     * Many-to-one relationship (one user can have multiple codes)
     *
     * Пользователь, связанный с этим кодом подтверждения
     * Отношение многие-к-одному (один пользователь может иметь несколько кодов)
     */
    @ManyToOne
    @JoinColumn(name = "account_id")
    private User user;

    /**
     * Default constructor
     * Конструктор по умолчанию
     */
    public ConfirmationCode() {
    }

    /**
     * Constructor with all fields
     * Конструктор со всеми полями
     *
     * @param value confirmation code value / значение кода подтверждения
     * @param expiration expiration date/time / дата/время истечения
     * @param user user associated with this code / пользователь, связанный с кодом
     */
    public ConfirmationCode(String value, LocalDateTime expiration, User user) {
        this.value = value;
        this.expiration = expiration;
        this.user = user;
    }

    // Getters and setters / Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // equals, hashCode, toString methods / методы equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ConfirmationCode confirmationCode)) {
            return false;
        }

        return id != null && Objects.equals(id, confirmationCode.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Confirmation code: id - %d, value - %s, expiration - %s, User email - %s",
                id,
                value,
                expiration == null ? "unknown" : expiration,
                user == null || user.getEmail() == null ? "unknown" : user.getEmail());
    }
}
