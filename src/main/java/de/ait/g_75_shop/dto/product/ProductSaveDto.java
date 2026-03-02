package de.ait.g_75_shop.dto.product;

import java.math.BigDecimal;

/**
 * DTO for saving new product
 * Contains basic product information needed for creation
 *
 * DTO для сохранения нового товара
 * Содержит основную информацию о товаре, необходимую для создания
 */
public class ProductSaveDto {

    private String title;
    private BigDecimal price;

    public ProductSaveDto() {
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

    @Override
    public String toString() {
        return String.format("Product Save DTO: title - %s, price - %.2f",
                title, price != null ? price : "null");
    }
}
