package de.ait.g_75_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Валидация имени покупателя:
     * - NotNull: имя не может быть null
     * - NotBlank: имя не может быть пустым или состоять только из пробелов
     * - Pattern: имя должно начинаться с заглавной буквы и содержать только буквы и пробелы
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

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "customer")
    private Cart cart;

    public Customer() {
        this.active = true;
    }

    // Геттеры и сеттеры
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