package de.ait.g_75_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Product entity representing items available for purchase
 * Contains product information like title, price, and availability
 *
 * Сущность товара, представляющая товары, доступные для покупки
 * Содержит информацию о товаре: название, цена и доступность
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Product title validation:
     * - NotNull: title cannot be null
     * - NotBlank: title cannot be empty
     * - Pattern: starts with capital letter, at least 3 characters, only letters and spaces
     *
     * Валидация названия товара:
     * - NotNull: название не может быть null
     * - NotBlank: название не может быть пустым
     * - Pattern: начинается с заглавной буквы, минимум 3 символа, только буквы и пробелы
     */
    @NotNull(message = "Product title cannot be null")
    @NotBlank(message = "Product title cannot be empty")
//    @Length(min = 3, max = 50)
    @Pattern(
            regexp = "[A-Z][a-z ]{2,99}",
            message = "Product title should be at least three characters length and starts with capital letter"
    )
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    /**
     * Product price validation:
     * - NotNull: price cannot be null
     * - DecimalMin: price must be >= 0.00
     * - DecimalMax: price must be < 1000.00 (exclusive)
     *
     * Валидация цены товара:
     * - NotNull: цена не может быть null
     * - DecimalMin: цена должна быть >= 0.00
     * - DecimalMax: цена должна быть < 1000.00 (исключительно)
     */
    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.00", message = "Product price should be greater or equal than 0")
    @DecimalMax(value = "1000.00", inclusive = false, message = "Product price should be lesser than 1000")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Soft delete flag - true means product is active and available for purchase
     * Флаг мягкого удаления - true означает, что товар активен и доступен для покупки
     */
    @Column(name = "active")
    private boolean active;

    /**
     * URL to product image
     * URL изображения товара
     */
    @Column(name = "image_url")
    private String imageUrl;

    public Product() {
        this.active = true; // New products are active by default / Новые товары активны по умолчани
    }

    // Getters and setters / Геттеры и сеттеры
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // equals, hashCode, toString methods / методы equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Product product)) {
            return false;
        }

        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Product: id - %d, title - %s, price - %.2f",
                id, title, price != null ? price : "null", active ? "yes" : "no");
    }
}