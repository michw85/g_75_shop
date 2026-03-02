package de.ait.g_75_shop.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Cart entity representing a shopping cart
 * Linked to Customer with one-to-one relationship
 * Contains multiple Position entities (cart items)
 *
 * Сущность корзины, представляющая корзину покупок
 * Связана с Customer отношением один-к-одному
 * Содержит множество сущностей Position (позиции корзины)
 */
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Collection of positions in the cart
     * EAGER fetching - loads positions immediately with cart
     * Cascade ALL - operations on cart affect positions
     *
     * Коллекция позиций в корзине
     * EAGER загрузка - загружает позиции сразу с корзиной
     * Cascade ALL - операции с корзиной влияют на позиции
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "cart")
    private Set<Position> positions = new HashSet<>();

    /**
     * Customer who owns this cart
     * One-to-one bidirectional relationship
     *
     * Покупатель, владеющий этой корзиной
     * Двунаправленное отношение один-к-одному
     */
    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    public Cart() {
    }

    // Getters and setters / Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Business methods / Бизнес-методы
    /**
     * Adds product to cart with specified quantity
     * If product already exists, increases quantity
     *
     * Добавляет товар в корзину с указанным количеством
     * Если товар уже существует, увеличивает количество
     *
     * @param product product to add / товар для добавления
     * @param quantity quantity to add / количество для добавления
     */
    public void addPosition(Product product, int quantity) {
        // Check if product already in cart / Проверяем, есть ли уже такой товар в корзине
        for (Position position : positions) {
            if (position.getProduct().equals(product)) {
                position.setQuantity(position.getQuantity() + quantity);
                return;
            }
        }

        //  Create new position if product not found / Если товара нет, создаем новую позицию
        Position position = new Position();
        position.setProduct(product);
        position.setQuantity(quantity);
        position.setCart(this);
        positions.add(position);
    }

    /**
     * Removes product from cart
     *
     * Удаляет товар из корзины
     *
     * @param product product to remove / товар для удаления
     */
    public void removePosition(Product product) {
        positions.removeIf(position -> position.getProduct().equals(product));
    }

    /**
     * Removes product from cart by product ID
     *
     * Удаляет товар из корзины по ID товара
     *
     * @param productId ID of product to remove / ID товара для удаления
     */
    public void removePositionById(Long productId) {
        positions.removeIf(position -> position.getProduct().getId().equals(productId));
    }

    /**
     * Clears all positions from cart
     *
     * Очищает все позиции из корзины
     */
    public void clearCart() {
        positions.clear();
    }

    /**
     * Calculates total price of all items in cart
     *
     * Вычисляет общую стоимость всех товаров в корзине
     *
     * @return total price / общая стоимость
     */
    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Position position : positions) {
            total = total.add(position.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(position.getQuantity())));
        }
        return total;
    }

    /**
     * Calculates average price per item in cart
     *
     * Вычисляет среднюю цену товара в корзине
     *
     * @return average price / средняя цена
     */
    public BigDecimal getAveragePrice() {
        if (positions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalQuantity = positions.stream()
                .mapToInt(Position::getQuantity)
                .sum();

        if (totalQuantity == 0) {
            return BigDecimal.ZERO;
        }

        return getTotalPrice().divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
    }

    // equals, hashCode, toString methods / методы equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart cart)) return false;
        return id != null && Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Cart: id - %d, customer - %s, positions count - %d, total price - %.2f",
                id, customer != null ? customer.getName() : "null",
                positions.size(), getTotalPrice());
    }


}