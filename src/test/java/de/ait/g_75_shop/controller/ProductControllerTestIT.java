package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.constants.Constants;
import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.domain.enums.Role;
import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import de.ait.g_75_shop.repository.ProductRepository;
import de.ait.g_75_shop.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static de.ait.g_75_shop.constants.Constants.ACCESS_TOKEN_COOKIE_NAME;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${KEY_PHRASE_ACCESS}")
    private String accessPhrase;

    private String adminAccessToken;

    // Base URLs / Повторяющиеся значения в разных методах рекомендуется вносить в константы класса
    private static final String PRODUCT_RESOURCE = "/products";
    private static final String AUTH_RESOURCE = "/auth";

// ===================== + Tests / ПОЗИТИВНЫЕ ТЕСТЫ =====================

    /**
     * Positive test: Save product with valid data and admin authentication
     * Позитивный тест: Сохранение продукта с валидными данными и аутентификацией администратора
     */

    @Test
    public void shouldSaveProduct() {
        // reate request body / Создаем тело запроса (им является в данном случае DTO для сохранения)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test product");
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создается кука и заголовки
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        // Create cookie and headers with admin token / Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        // Send request and get response / Отправляем запрос и получаем ответ (response)
        ResponseEntity<ProductDto> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, ProductDto.class
        );

        // Verify response status / Проверяем, что нам действительно пришёл ожидаемый статус ответа
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        // Verify response body / Проверяем корректность того, что нам пришло в теле ответа
        ProductDto dto = response.getBody();
        assertNotNull(dto, "Response body shouldn't be null");
        assertNotNull(dto.getId(), "Returned product Id shouldn't be null");
        assertEquals(saveDto.getTitle(), dto.getTitle(), "Returned product has incorrect title");
        assertEquals(saveDto.getPrice(), dto.getPrice(), "Returned product has incorrect price");

        // Verify product was saved in database / Проверяем, что продукт корректно сохранился в БД
        Product savedProduct = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
        assertNotNull(savedProduct, "Product wasn't properly saved to the db");
        assertEquals(saveDto.getTitle(), savedProduct.getTitle(), "Saved product has incorrect title");
        assertEquals(saveDto.getPrice(), savedProduct.getPrice(), "Saved product has incorrect price");
    }

    /**
     * Positive test: Get all active products
     * Позитивный тест: Получение всех активных продуктов
     */
    @Test
    public void shouldGetAllActiveProducts() {
        // Send request without authentication (public endpoint)
        // Отправляем запрос без аутентификации (публичный endpoint)
        ResponseEntity<ProductDto[]> response = httpClient.getForEntity(
                PRODUCT_RESOURCE, ProductDto[].class
        );

        // Verify response
        // Проверяем ответ
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");

        ProductDto[] products = response.getBody();
        assertNotNull(products, "Response body shouldn't be null");

        // Should return only active product (1 out of 2 created in setUp)
        // Должен вернуться только активный продукт (1 из 2 созданных в setUp)
        assertEquals(1, products.length, "Should return only active products");
    }

    /**
     * Positive test: Get product by ID
     * Позитивный тест: Получение продукта по ID
     */
    @Test
    public void shouldGetProductById() {
        // Get the active product from database
        // Получаем активный продукт из БД
        Product activeProduct = repository.findAllByActiveTrue().get(0);

        // Send request without authentication (public endpoint)
        // Отправляем запрос без аутентификации (публичный endpoint)
        ResponseEntity<ProductDto> response = httpClient.getForEntity(
                PRODUCT_RESOURCE + "/" + activeProduct.getId(), ProductDto.class
        );

        // Verify response
        // Проверяем ответ
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");

        ProductDto product = response.getBody();
        assertNotNull(product, "Response body shouldn't be null");
        assertEquals(activeProduct.getId(), product.getId(), "Returned product has incorrect ID");
        assertEquals(activeProduct.getTitle(), product.getTitle(), "Returned product has incorrect title");
    }

    // ===================== - Tests / НЕГАТИВНЫЕ ТЕСТЫ =====================

    // Negative test: Save product with empty title / Тестируем негативный сценарий - что будет, если не будет title
    @Test
    public void shouldReturn400WhenTitleIsEmpty(){
        // Create request body with empty title / Создаем тело запроса (им является в данном случае DTO для сохранения)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("");
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создается кука и заголовки
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        // Create request with admin token (important! without token will get 403) Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        // Send request and get response / Отправляем запрос и получаем ответ (response)
        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, String.class
        );

        // Verify response status - should be 400 BAD REQUEST, not 403 / Проверяем, что нам действительно пришёл ожидаемый статус ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response has unexpected status");

        // Verify response body contains error message / Проверяем корректность того, что нам пришло в теле ответа
        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("title"), "Response body doesn't contain expected message");
    }

    /**
     * Negative test: Save product with title that doesn't match pattern
     * Негативный тест: Сохранение продукта с названием, не соответствующим паттерну
     */
    @Test
    public void shouldReturn400WhenTitleInvalid() {
        // Create request body with invalid title (lowercase)
        // Создаем тело запроса с неверным названием (строчные буквы)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("invalid title"); // Should start with capital letter
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создается кука и заголовки
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");
    }

    /**
     * Negative test: Save product with negative price
     * Негативный тест: Сохранение продукта с отрицательной ценой
     */
    @Test
    public void shouldReturn400WhenPriceIsNegative() {
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Valid Title");
        saveDto.setPrice(new BigDecimal("-10.00"));

        // Создается кука и заголовки
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");
    }

    /**
     * Negative test: Save product with price exceeding maximum
     * Негативный тест: Сохранение продукта с ценой превышающей максимум
     */
    @Test
    public void shouldReturn400WhenPriceTooHigh() {
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Valid Title");
        saveDto.setPrice(new BigDecimal("10000.00")); // > 1000

        // Создается кука и заголовки
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCT_RESOURCE, request, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");
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

    @BeforeEach
    public void setUp() {
        addUsersToDB();
        createAdminAccessToken();
    }

    private void createAdminAccessToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 60000); // 60 секунд

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(accessPhrase));

        adminAccessToken = Jwts.builder()
                .subject("admin@test.com")
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private void addUsersToDB() {
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("adminPass"));
        admin.setName("Admin");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setConfirmed(true);
        userRepository.save(admin);
    }

    // Метод для очистки БД после каждого теста
    @AfterEach
    public void cleanDatabase(){
        repository.deleteAll();
        userRepository.deleteAll();
    }
}