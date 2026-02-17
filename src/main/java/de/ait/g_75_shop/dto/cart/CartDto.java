package de.ait.g_75_shop.dto.cart;
import de.ait.g_75_shop.dto.position.PositionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;

@Schema(description = "Cart DTO for sending to Client")
public class CartDto {

    @Schema(description = "Cart unique identifier", example = "1")
    private Long id;

    @Schema(description = "Cart positions")
    private Set<PositionDto> positions;

    @Schema(description = "Total price of all items in cart", example = "250.50")
    private BigDecimal totalPrice;

    @Schema(description = "Average price per item in cart", example = "125.25")
    private BigDecimal averagePrice;

    @Schema(description = "Total quantity of items in cart", example = "2")
    private int totalQuantity;

    public CartDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public String toString() {
        return String.format("Cart DTO: id - %d, positions - %s, total price - %.2f, avg price - %.2f, total qty - %d",
                id, positions != null ? positions.size() : 0, totalPrice, averagePrice, totalQuantity);
    }
}