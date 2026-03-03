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



    @Test
    public void shouldSaveProduct() {
        // Создаем тело запроса (им является в данном случае DTO для сохранения)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test product");
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создается кука и заголовки
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        // Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

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