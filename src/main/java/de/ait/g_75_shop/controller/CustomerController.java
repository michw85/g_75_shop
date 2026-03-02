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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for managing customers
 * All endpoints start with /customers
 * <p>
 * REST контроллер для работы с покупателями
 * Все endpoints начинаются с /customers
 */
@RestController
@RequestMapping("/customers")
@Tag(name = "Customer controller", description = "Controller for various operations with Customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Constructor with dependency injection
     * Конструктор с внедрением зависимости
     *
     * @param customerService service for customer operations / сервис для операций с покупателями
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Creates a new customer
     * POST /customers - создание нового покупателя
     *
     * @param saveDto new customer data with validation / данные нового покупателя (с валидацией)
     * @return created customer / созданный покупатель
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
     * Gets all active customers
     * GET /customers - получение всех активных покупателей
     *
     * @return list of active customers / список активных покупателей
     */
    @GetMapping
    @Operation(summary = "Get all Customers", description = "Get all active Customers from Database")
    public List<CustomerDto> getAll() {
        return customerService.getAllActiveCustomers();
    }

    /**
     * GET /customers/{id} - получение покупателя по ID
     *
     * @param id customer identifier / идентификатор покупателя
     * @return customer DTO / DTO покупателя
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
     * PUT /customers/{id} - Updates customer name / обновление имени покупателя
     *
     * @param id        customer identifier / идентификатор покупателя
     * @param updateDto new customer data with validation / новые данные (с валидацией)
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
     * DELETE /customers/{id} - Soft deletes customer by ID / мягкое удаление покупателя
     *
     * @param id customer identifier / идентификатор покупателя
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
     * PUT /customers/{id}/restore - Restores previously deleted customer / восстановление удаленного покупателя
     *
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
     * GET /customers/count -  Gets count of active customers / количество активных покупателей
     *
     * @return number of active customers / количество активных покупателей
     */
    @GetMapping("/count")
    @Operation(summary = "Get Customers count", description = "Get total count of active Customers")
    public long getCustomersQuantity() {
        return customerService.getAllActiveCustomersCount();
    }

    /**
     * GET /customers/{id}/cart/total-cost -  Gets total cost of customer's cart / общая стоимость корзины
     *
     * @param id идентификатор покупателя
     * @return total cart cost / общая стоимость корзины
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
     * GET /customers/{id}/cart/avg-price - Gets average price in customer's cart / средняя цена в корзине
     *
     * @param id customer identifier / идентификатор покупателя
     * @return average price / средняя цена
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
     * POST /customers/{customerId}/cart/products/{productId} - Adds product to customer's cart / добавление товара в корзину
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @param productId  product identifier /  идентификатор товара
     * @param quantity   quantity to add (default: 1) /  количество (по умолчанию 1)
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
     * DELETE /customers/{customerId}/cart/products/{productId} - Removes product from customer's cart / удаление товара из корзины
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @param productId  product identifier / идентификатор товара
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
     * DELETE /customers/{id}/cart - Clears customer's cart completely / очистка корзины
     *
     * @param id customer identifier / идентификатор покупателя
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

    /**
     * Adds image to customer profile
     * POST /customers/{id}/image with multipart/form-data
     *
     * Добавляет изображение в профиль покупателя
     *
     * @param id customer identifier / идентификатор покупателя
     * @param image image file to upload / файл изображения для загрузки
     * @throws IOException if file processing fails / если ошибка обработки файла
     */
    // POST -> http://10.20.30.40:8081/products/7/image
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public void addImage(@PathVariable Long id, @RequestParam MultipartFile image) throws IOException {
        customerService.addImage(id, image);
    }
}