package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.dto.customer.CustomerDto;
import de.ait.g_75_shop.dto.customer.CustomerSaveDto;
import de.ait.g_75_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_75_shop.service.interfaces.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST контроллер для работы с покупателями
 * Все endpoints начинаются с /customers
 */
@RestController
@RequestMapping("/customers")
@Tag(name = "Customer controller", description = "Controller for various operations with Customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * POST /customers - создание нового покупателя
     * @param saveDto данные нового покупателя (с валидацией)
     * @return созданный покупатель
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save Customer", description = "Save new Customer to the Database")
    public CustomerDto save(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Body with new Customer parameters")
            CustomerSaveDto saveDto
    ) {
        return customerService.save(saveDto);
    }

    /**
     * GET /customers - получение всех активных покупателей
     */
    @GetMapping
    @Operation(summary = "Get all Customers", description = "Get all active Customers from Database")
    public List<CustomerDto> getAll() {
        return customerService.getAllActiveCustomers();
    }

    /**
     * GET /customers/{id} - получение покупателя по ID
     * @param id идентификатор покупателя
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Customer by ID", description = "Get active Customer by its identifier")
    public CustomerDto getById(
            @PathVariable
            @Parameter(description = "Customer ID to search")
            Long id
    ) {
        return customerService.getActiveCustomerById(id);
    }

    /**
     * PUT /customers/{id} - обновление имени покупателя
     * @param id идентификатор покупателя
     * @param updateDto новые данные (с валидацией)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Customer", description = "Update Customer name by its identifier")
    public void update(
            @PathVariable
            @Parameter(description = "Customer ID to update")
            Long id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Body with new Customer name")
            CustomerUpdateDto updateDto
    ) {
        customerService.update(id, updateDto);
    }

    /**
     * DELETE /customers/{id} - мягкое удаление покупателя
     * @param id идентификатор покупателя
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Customer", description = "Soft delete Customer by its identifier")
    public void deleteById(
            @PathVariable
            @Parameter(description = "Customer ID to delete")
            Long id
    ) {
        customerService.deleteById(id);
    }

    /**
     * PUT /customers/{id}/restore - восстановление удаленного покупателя
     * @param id идентификатор покупателя
     */
    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore Customer", description = "Restore previously deleted Customer by its identifier")
    public void restoreById(
            @PathVariable
            @Parameter(description = "Customer ID to restore")
            Long id
    ) {
        customerService.restoreById(id);
    }

    /**
     * GET /customers/count - количество активных покупателей
     */
    @GetMapping("/count")
    @Operation(summary = "Get Customers count", description = "Get total count of active Customers")
    public long getCustomersQuantity() {
        return customerService.getAllActiveCustomersCount();
    }

    /**
     * GET /customers/{id}/cart/total-cost - общая стоимость корзины
     * @param id идентификатор покупателя
     */
    @GetMapping("/{id}/cart/total-cost")
    @Operation(summary = "Get Cart total cost", description = "Get total cost of Customer's cart")
    public BigDecimal getCustomerCartTotalCost(
            @PathVariable
            @Parameter(description = "Customer ID")
            Long id
    ) {
        return customerService.getCustomerCartTotalCost(id);
    }

    /**
     * GET /customers/{id}/cart/avg-price - средняя цена в корзине
     * @param id идентификатор покупателя
     */
    @GetMapping("/{id}/cart/avg-price")
    @Operation(summary = "Get Cart average price", description = "Get average price of products in Customer's cart")
    public BigDecimal getCustomerCartAveragePrice(
            @PathVariable
            @Parameter(description = "Customer ID")
            Long id
    ) {
        return customerService.getCustomerCartAveragePrice(id);
    }

    /**
     * POST /customers/{customerId}/cart/products/{productId} - добавление товара в корзину
     * @param customerId идентификатор покупателя
     * @param productId идентификатор товара
     * @param quantity количество (по умолчанию 1)
     */
    @PostMapping("/{customerId}/cart/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add product to cart", description = "Add product to Customer's cart by their identifiers")
    public void addProductToCart(
            @PathVariable
            @Parameter(description = "Customer ID")
            Long customerId,
            @PathVariable
            @Parameter(description = "Product ID to add")
            Long productId,
            @RequestParam(defaultValue = "1")
            @Parameter(description = "Quantity of product (default: 1)")
            int quantity
    ) {
        customerService.addProductToCart(customerId, productId, quantity);
    }

    /**
     * DELETE /customers/{customerId}/cart/products/{productId} - удаление товара из корзины
     * @param customerId идентификатор покупателя
     * @param productId идентификатор товара
     */
    @DeleteMapping("/{customerId}/cart/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove product from cart", description = "Remove product from Customer's cart by their identifiers")
    public void removeProductFromCart(
            @PathVariable
            @Parameter(description = "Customer ID")
            Long customerId,
            @PathVariable
            @Parameter(description = "Product ID to remove")
            Long productId
    ) {
        customerService.removeProductFromCart(customerId, productId);
    }

    /**
     * DELETE /customers/{id}/cart - очистка корзины
     * @param id идентификатор покупателя
     */
    @DeleteMapping("/{id}/cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Clear cart", description = "Completely clear Customer's cart")
    public void clearCart(
            @PathVariable
            @Parameter(description = "Customer ID")
            Long id
    ) {
        customerService.clearCart(id);
    }
}