package de.ait.g_75_shop.domain;

import de.ait.g_75_shop.domain.Cart;
import de.ait.g_75_shop.domain.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Objects;

/**
 * Position entity representing a single item in shopping cart
 * Links product with quantity in specific cart
 *
 * Сущность позиции, представляющая отдельный товар в корзине покупок
 * Связывает товар с количеством в конкретной корзине
 */
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Product in this cart position (many-to-one relationship)
     * Товар в этой позиции корзины (отношение многие-к-одному)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity validation:
     * - Min: at least 1 item
     * - Max: maximum 100 items per position (cart limit)
     *
     * Валидация количества товара:
     * - NotNull: количество не может быть null (для примитива int не нужно)
     * - Min: минимум 1 товар
     * - Max: максимум 100 товаров на позицию (ограничение корзины)
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100 items per position")
    @Column(name = "quantity")
    private int quantity;

    /**
     * Cart that contains this position (many-to-one relationship)
     * Корзина, содержащая эту позицию (отношение многие-к-одному)
     */
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public Position() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    // Getters and setters / Геттеры и сеттеры
    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // equals, hashCode, toString methods / методы equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Position position)) {
            return false;
        }

        return id != null && Objects.equals(id, position.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Position: id - %d, product - %s, quantity - %d", id, product, quantity);
    }
}
