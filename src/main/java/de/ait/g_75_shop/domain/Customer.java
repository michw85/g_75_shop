package de.ait.g_75_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer entity representing a buyer in the system
 * Contains personal information and shopping cart
 *
 * Сущность покупателя, представляющая покупателя в системе
 * Содержит личную информацию и корзину покупок
 */
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Customer name validation rules:
     * - NotNull: name cannot be null
     * - NotBlank: name cannot be empty or only spaces
     * - Pattern: must start with capital letter, contain only letters and spaces
     * - Size: length between 2 and 50 characters
     *
     * Валидация имени покупателя:
     * - NotNull: имя не может быть null
     * - NotBlank: имя не может быть пустым или состоять только из пробелов
     * - Pattern: должно начинаться с заглавной буквы, содержать только буквы и пробелы
     * - Size: длина от 2 до 50 символов
     */
    @NotNull(message = "Customer name cannot be null")
    @NotBlank(message = "Customer name cannot be empty")
    @Pattern(
            regexp = "[A-Z][a-z]+( [A-Z][a-z]+)*",
            message = "Customer name should start with capital letter and contain only letters"
    )
    @Size(min = 2, max = 50, message = "Customer name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Soft delete flag - true means customer is active
     * Флаг мягкого удаления - true означает, что покупатель активен
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * URL to customer's profile image
     * URL изображения профиля покупателя
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Customer's shopping cart (one-to-one relationship)
     * Корзина покупок покупателя (отношение один-к-одному)
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "customer")
    private Cart cart;

    public Customer() {
        this.active = true;
    }

    // Getters and setters / Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // equals, hashCode, toString methods / методы equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Customer: id - %d, name - %s, active - %s",
                id, name, active ? "yes" : "no");
    }
}