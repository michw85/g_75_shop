package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import de.ait.g_75_shop.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private ProductRepository repository;

    private static final String PRODUCT_RESOURCE = "/products";

    @Test
    public void shouldSaveProduct() {
        // Создаем тело запроса (им является в данном случае DTO для сохранения)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test product");
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto);

        // Отправляем запрос и получаем ответ (response)
        ResponseEntity<ProductDto> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, ProductDto.class
        );

        // Проверяем, что нам действительно пришёл ожидаемый статус ответа
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        // Проверяем корректность того, что нам пришло в теле ответа
        ProductDto dto = response.getBody();
        assertNotNull(dto, "Response body shouldn't be null");
        assertNotNull(dto.getId(), "Returned product Id shouldn't be null");
        assertEquals(saveDto.getTitle(), dto.getTitle(), "Returned product has incorrect title");
        assertEquals(saveDto.getPrice(), dto.getPrice(), "Returned product has incorrect price");

        // Проверяем, что продукт корректно сохранился в БД
        Product savedProduct = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
        assertNotNull(savedProduct, "Product wasn't properly saved to the db");
        assertEquals(saveDto.getTitle(), savedProduct.getTitle(), "Saved product has incorrect title");
        assertEquals(saveDto.getPrice(), savedProduct.getPrice(), "Saved product has incorrect price");
    }

    // Тестируем негативный сценарий - что будет, если не будет title
    @Test
    public void shouldReturn400WhenTitleIsEmpty(){
        // Создаем тело запроса (им является в данном случае DTO для сохранения)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("");
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto);

        // Отправляем запрос и получаем ответ (response)
        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, String.class
        );

        // Проверяем, что нам действительно пришёл ожидаемый статус ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response has unexpected status");

        // Проверяем корректность того, что нам пришло в теле ответа
        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("title"), "Response body doesn't contain expected message");
    }

    // Метод для заполнения базы данных тестовыми объектами перед выполнением каждого теста
    @BeforeEach
    public void startUp(){
        Product activeProduct = new Product();
        activeProduct.setTitle("Test active product");
        activeProduct.setPrice(new BigDecimal("111.00"));
        activeProduct.setActive(true);

        Product inActiveProduct = new Product();
        inActiveProduct.setTitle("Test inactive product");
        inActiveProduct.setPrice(new BigDecimal("222.00"));
        inActiveProduct.setActive(false);

        repository.saveAll(List.of(activeProduct, inActiveProduct));
    }

    // Метод для очистки БД после каждого теста
    @AfterEach
    public void cleanDatabase(){
        repository.deleteAll();
    }
}