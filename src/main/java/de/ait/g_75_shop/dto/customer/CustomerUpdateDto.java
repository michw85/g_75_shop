package de.ait.g_75_shop.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO для обновления имени покупателя
 * Содержит валидацию нового имени
 */
@Schema(description = "Customer DTO for updating Customer name in Database")
public class CustomerUpdateDto {

    @Schema(description = "New customer name", example = "Petr Petrov", required = true)
    @NotNull(message = "New customer name cannot be null")
    @NotBlank(message = "New customer name cannot be empty")
    @Pattern(
            regexp = "[A-Z][a-z]+( [A-Z][a-z]+)*",
            message = "New customer name should start with capital letter and contain only letters"
    )
    @Size(min = 2, max = 50, message = "New customer name must be between 2 and 50 characters")
    private String newName;

    public CustomerUpdateDto() {
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return String.format("Customer Update DTO: new name - %s", newName);
    }
}