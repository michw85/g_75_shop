package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import de.ait.g_75_shop.dto.product.ProductUpdateDto;
import de.ait.g_75_shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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

    public ProductController(ProductService service) {
        this.service = service;
    }

    //   Сохранить продукт в базе данных (при сохранении продукт автоматически считается активным).
    // POST -> http://10.20.30.40:8081/products -> ожидаем данные продукта в теле запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save Product", description = "Save new Product to the Database")
    public ProductDto save(@RequestBody
                           @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Body with new Product parameters")
                           ProductSaveDto saveDto) {
        return service.save(saveDto);
    }

    // • Вернуть все продукты из базы данных (активные).
    // GET -> http://10.20.30.40:8081/products
    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAllActiveProducts();
    }

    // • Вернуть один продукт из базы данных по его идентификатору (если он активен).
    // GET -> http://10.20.30.40:8081/products/5
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable  @Parameter(description = "Product ID to search") Long id) {
        return service.getActiveProductById(id);
    }

    // • Изменить один продукт в базе данных по его идентификатору.
    // PUT -> http://10.20.30.40:8081/products/5
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProductUpdateDto updateDto) {
        service.update(id, updateDto);
    }

    // • Удалить продукт из базы данных по его идентификатору.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);

    }

    // • Восстановить удалённый продукт в базе данных по его идентификатору.
    // PUT -> http://10.20.30.40:8081/products/5/restore
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    //    Вернуть общее количество продуктов в базе данных (активных).
    @GetMapping("/count")
    public long getProductsCount() {
        return service.getAllActiveProductsCount();
    }

    // • Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    @GetMapping("/total-coast")
    public BigDecimal getProductsTotalCoast() {
        return service.getAllActiveProductsTotalCost();
    }

    // • Вернуть среднюю стоимость продукта в базе данных (из активных).
    @GetMapping("/avg")
    public BigDecimal getProductsAveragePrice() {
        return service.getAllActiveProductsAveragePrice();
    }
}
