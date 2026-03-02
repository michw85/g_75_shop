package de.ait.g_75_shop.dto.product;

import java.math.BigDecimal;

/**
 * DTO for updating product
 * Currently only supports price update
 *
 * DTO для обновления товара
 * В настоящее время поддерживает только обновление цены
 */
public class ProductUpdateDto {

    private BigDecimal newPrice;

    public ProductUpdateDto() {
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return String.format("Product Update DTO: new price - %.2f",
               newPrice != null ? newPrice : "null");
    }
}
