package de.ait.g_75_shop.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO для сохранения нового покупателя
 * Содержит валидацию полей
 */
@Schema(description = "Customer DTO for saving new Customer to Database")
public class CustomerSaveDto {

    @Schema(description = "Customer name", example = "Ivan Ivanov", required = true)
    @NotNull(message = "Customer name cannot be null")
    @NotBlank(message = "Customer name cannot be empty")
    @Pattern(
            regexp = "[A-Z][a-z]+( [A-Z][a-z]+)*",
            message = "Customer name should start with capital letter and contain only letters"
    )
    @Size(min = 2, max = 50, message = "Customer name must be between 2 and 50 characters")
    private String name;

    public CustomerSaveDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Customer Save DTO: name - %s", name);
    }
}