package de.ait.g_75_shop.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "cart")
    private Set<Position> positions = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    public Cart() {
    }

    // Геттеры и сеттеры
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

    // Бизнес-методы
    public void addPosition(Product product, int quantity) {
        // Проверяем, есть ли уже такой товар в корзине
        for (Position position : positions) {
            if (position.getProduct().equals(product)) {
                position.setQuantity(position.getQuantity() + quantity);
                return;
            }
        }

        // Если товара нет, создаем новую позицию
        Position position = new Position();
        position.setProduct(product);
        position.setQuantity(quantity);
        position.setCart(this);
        positions.add(position);
    }

    public void removePosition(Product product) {
        positions.removeIf(position -> position.getProduct().equals(product));
    }

    public void removePositionById(Long productId) {
        positions.removeIf(position -> position.getProduct().getId().equals(productId));
    }

    public void clearCart() {
        positions.clear();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Position position : positions) {
            total = total.add(position.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(position.getQuantity())));
        }
        return total;
    }

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