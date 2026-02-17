package de.ait.g_75_shop.dto.position;
import de.ait.g_75_shop.dto.product.ProductDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Position DTO for sending to Client")
public class PositionDto {

    @Schema(description = "Position unique identifier", example = "1")
    private Long id;

    @Schema(description = "Product in this position")
    private ProductDto product;

    @Schema(description = "Quantity of product", example = "2")
    private int quantity;

    @Schema(description = "Total price for this position", example = "240.00")
    private BigDecimal totalPrice;

    public PositionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return String.format("Position DTO: id - %d, product - %s, quantity - %d, total price - %.2f",
                id, product != null ? product.getTitle() : "null", quantity, totalPrice);
    }
}
