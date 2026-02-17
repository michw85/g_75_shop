package de.ait.g_75_shop.dto.product;

import java.math.BigDecimal;

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
