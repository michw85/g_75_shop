package de.ait.g_75_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Product title cannot be null")
    @NotBlank(message = "Product title cannot be empty")
//    @Length(min = 3, max = 50)
    @Pattern(
            regexp = "[A-Z][a-z ]{2,99}",
            message = "Product title should be at least three characters length and starts with capital letter"
    )
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.00", message = "Product price should be greater or equal than 0")
    @DecimalMax(value = "1000.00", inclusive = false, message = "Product price should be lesser than 1000")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "active")
    private boolean active;

    @Column(name = "image_url")
    private String imageUrl;

    public Product() {
        this.active = true;
    }

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