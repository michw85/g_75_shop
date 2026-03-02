package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import de.ait.g_75_shop.dto.product.ProductUpdateDto;
import de.ait.g_75_shop.service.interfaces.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
/**
 * REST Controller for managing products
 * All requests starting with /products are handled by this controller
 *
 * REST контроллер для управления товарами
 * Все запросы, начинающиеся с /products, обрабатываются этим контроллером
 */
/*
@RequestMapping("/products") - благодаря этой аннотации Spring понимает,
что все запросы, которые пришли на http://10.20.30.40:8080/products
нужно адресовать именно этому контроллеру
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Product controller", description = "Controller for various operations with Products")
public class ProductController {

    // Здесь будет поле, содержащее объект сервиса продуктов
    private final ProductService service;
    /**
     * Constructor with dependency injection
     * Конструктор с внедрением зависимости
     *
     * @param service product service / сервис продуктов
     */
    public ProductController(ProductService service) {
        this.service = service;
    }

    /**
     * Saves new product to database (automatically active)
     * POST /products
     *
     * Сохраняет новый товар в базу данных (автоматически активный)
     *
     * @param saveDto product data in request body / данные товара в теле запроса
     * @return saved product DTO / сохраненный товар в виде DTO
     */
    // POST -> http://10.20.30.40:8081/products -> ожидаем данные продукта в теле запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save Product", description = "Save new Product to the Database")
    public ProductDto save(@RequestBody
                           @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Body with new Product parameters")
                           ProductSaveDto saveDto) {
        return service.save(saveDto);
    }

    /**
     * Returns all active products from database
     * GET /products
     *
     * Возвращает все активные товары из базы данных
     *
     * @return list of active product DTOs / список активных товаров в виде DTO
     */
    // GET -> http://10.20.30.40:8081/products
    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAllActiveProducts();
    }

    /**
     * Returns one active product by its identifier
     * GET /products/{id}
     *
     * Возвращает один активный товар по его идентификатору
     *
     * @param id product identifier / идентификатор товара
     * @return product DTO / товар в виде DTO
     */
    // GET -> http://10.20.30.40:8081/products/5
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable  @Parameter(description = "Product ID to search") Long id) {
        return service.getActiveProductById(id);
    }

    /**
     * Updates product in database by its identifier
     * PUT /products/{id}
     *
     * Обновляет товар в базе данных по его идентификатору
     *
     * @param id product identifier / идентификатор товара
     * @param updateDto new product data / новые данные товара
     */
    // PUT -> http://10.20.30.40:8081/products/5
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProductUpdateDto updateDto) {
        service.update(id, updateDto);
    }

    /**
     * Soft deletes product by its identifier
     * DELETE /products/{id}
     *
     * Мягкое удаление товара по его идентификатору
     *
     * @param id product identifier / идентификатор товара
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);

    }

    /**
     * Restores previously deleted product
     * PUT /products/{id}/restore
     *
     * Восстанавливает ранее удаленный товар
     *
     * @param id product identifier / идентификатор товара
     */
    // PUT -> http://10.20.30.40:8081/products/5/restore
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    /**
     * Returns total count of active products
     * GET /products/count
     *
     * Возвращает общее количество активных товаров
     *
     * @return count of active products / количество активных товаров
     */
    @GetMapping("/count")
    public long getProductsCount() {
        return service.getAllActiveProductsCount();
    }

    /**
     * Returns total cost of all active products
     * GET /products/total-coast
     *
     * Возвращает суммарную стоимость всех активных товаров
     *
     * @return total cost / общая стоимость
     */
    @GetMapping("/total-coast")
    public BigDecimal getProductsTotalCoast() {
        return service.getAllActiveProductsTotalCost();
    }

    /**
     * Returns average price of active products
     * GET /products/avg
     *
     * Возвращает среднюю стоимость активных товаров
     *
     * @return average price / средняя цена
     */
    @GetMapping("/avg")
    public BigDecimal getProductsAveragePrice() {
        return service.getAllActiveProductsAveragePrice();
    }

    /**
     * Adds image to specific product by its identifier
     * POST /products/{id}/image with multipart/form-data
     *
     * Добавляет изображение к конкретному товару по его идентификатору
     *
     * @param id product identifier / идентификатор товара
     * @param image image file to upload / файл изображения для загрузки
     * @throws IOException if file processing fails / если ошибка обработки файла
     */
    // POST -> http://10.20.30.40:8081/products/7/image
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public void addImage(@PathVariable Long id, @RequestParam MultipartFile image) throws IOException {
        service.addImage(id, image);
    }
}
