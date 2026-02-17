package de.ait.g_75_shop.dto.customer;

import de.ait.g_75_shop.dto.cart.CartDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer DTO for sending to Client")
public class CustomerDto {

    @Schema(description = "Customer unique identifier", example = "1")
    private Long id;

    @Schema(description = "Customer name", example = "Ivan Ivanov")
    private String name;

    @Schema(description = "Customer cart", example = "Cart information")
    private CartDto cart;

    public CustomerDto() {
    }

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

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return String.format("Customer DTO: id - %d, name - %s, cart - %s",
                id, name, cart != null ? cart.toString() : "null");
    }
}