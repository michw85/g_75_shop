package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.constants.Constants;
import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.domain.enums.Role;
import de.ait.g_75_shop.dto.user.UserRegistrationDto;
import de.ait.g_75_shop.repository.UserRepository;
import de.ait.g_75_shop.security.dto.LoginRequestDto;
import de.ait.g_75_shop.security.dto.TokenResponseDto;
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
import java.util.Date;

import static de.ait.g_75_shop.constants.Constants.ACCESS_TOKEN_COOKIE_NAME;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${KEY_PHRASE_ACCESS}")
    private String accessPhrase;

    private String adminAccessToken;

    // Base URLs
    private static final String USER_RESOURCE = "/users";
    private static final String AUTH_RESOURCE = "/auth";

    /**
     * Positive test: Register new user successfully
     * Позитивный тест: Успешная регистрация нового пользователя
     */
    @Test
    public void shouldRegisterUser() {
        // Create request body (UserRegistrationDto)
        // Создаем тело запроса (UserRegistrationDto)
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("newuser@test.com");
        registrationDto.setPassword("password123");
        registrationDto.setName("New User");

        // Create HTTP request object
        // Создаем объект http-запроса
        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto);

        // Send request and get response
        // Отправляем запрос и получаем ответ
        ResponseEntity<String> response = httpClient.postForEntity(
                USER_RESOURCE + "/register", request, String.class
        );

        // Verify response status
        // Проверяем статус ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");

        // Verify response body
        // Проверяем тело ответа
        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("Registration complete"),
                "Response body doesn't contain expected message");

        // Verify user was saved in database
        // Проверяем, что пользователь сохранился в БД
        User savedUser = userRepository.findByEmail("newuser@test.com").orElse(null);
        assertNotNull(savedUser, "User wasn't properly saved to the db");
        assertEquals("newuser@test.com", savedUser.getEmail(), "Saved user has incorrect email");
        assertEquals("New User", savedUser.getName(), "Saved user has incorrect name");
        assertFalse(savedUser.isConfirmed(), "New user should not be confirmed");
        assertEquals(Role.ROLE_USER, savedUser.getRole(), "New user should have USER role");
        assertNotEquals("password123", savedUser.getPassword(), "Password should be encrypted");
    }

    /**
     * Positive test: Login with correct credentials
     * Позитивный тест: Вход с правильными учетными данными
     */
    @Test
    public void shouldLoginUser() {
        // Create request body (LoginRequestDto)
        // Создаем тело запроса (LoginRequestDto)
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setEmail("admin@test.com");
        loginDto.setPassword("adminPass");

        // Create HTTP request object
        // Создаем объект http-запроса
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginDto);

        // Send request and get response
        // Отправляем запрос и получаем ответ
        ResponseEntity<TokenResponseDto> response = httpClient.postForEntity(
                AUTH_RESOURCE + "/login", request, TokenResponseDto.class
        );

        // Verify response status
        // Проверяем статус ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has unexpected status");

        // Verify response body
        // Проверяем тело ответа
        TokenResponseDto tokens = response.getBody();
        assertNotNull(tokens, "Response body shouldn't be null");
        assertNotNull(tokens.getAccessToken(), "Access token shouldn't be null");
        assertNotNull(tokens.getRefreshToken(), "Refresh token shouldn't be null");
    }

    /**
     * Negative test: Register with existing email
     * Негативный тест: Регистрация с существующим email
     */
    @Test
    public void shouldReturn400WhenEmailExists() {
        // First, ensure admin user exists (created in setUp)
        // Сначала убеждаемся, что admin пользователь существует (создан в setUp)

        // Try to register with admin's email
        // Пытаемся зарегистрироваться с email администратора
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("admin@test.com");
        registrationDto.setPassword("newpass123");
        registrationDto.setName("Another Admin");

        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto);

        ResponseEntity<String> response = httpClient.postForEntity(
                USER_RESOURCE + "/register", request, String.class
        );

        // Verify response
        // Проверяем ответ
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");

        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("already in use"),
                "Response body doesn't contain expected message");
    }

    /**
     * Negative test: Register with empty email
     * Негативный тест: Регистрация с пустым email
     */
    @Test
    public void shouldReturn400WhenEmailIsEmpty() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("");
        registrationDto.setPassword("password123");
        registrationDto.setName("Test User");

        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto);

        ResponseEntity<String> response = httpClient.postForEntity(
                USER_RESOURCE + "/register", request, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");

        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
    }

    /**
     * Negative test: Register with empty password
     * Негативный тест: Регистрация с пустым паролем
     */
    @Test
    public void shouldReturn400WhenPasswordIsEmpty() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@test.com");
        registrationDto.setPassword("");
        registrationDto.setName("Test User");

        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto);

        ResponseEntity<String> response = httpClient.postForEntity(
                USER_RESOURCE + "/register", request, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");
    }

    /**
     * Negative test: Register with empty name
     * Негативный тест: Регистрация с пустым именем
     */
    @Test
    public void shouldReturn400WhenNameIsEmpty() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@test.com");
        registrationDto.setPassword("password123");
        registrationDto.setName("");

        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto);

        ResponseEntity<String> response = httpClient.postForEntity(
                USER_RESOURCE + "/register", request, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response has unexpected status");
    }

    /**
     * Negative test: Login with wrong password
     * Негативный тест: Вход с неправильным паролем
     */
    @Test
    public void shouldReturn401WhenPasswordIsWrong() {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setEmail("admin@test.com");
        loginDto.setPassword("wrongpass");

        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginDto);

        ResponseEntity<TokenResponseDto> response = httpClient.postForEntity(
                AUTH_RESOURCE + "/login", request, TokenResponseDto.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                "Response has unexpected status");
    }

    /**
     * Negative test: Login with non-existent email
     * Негативный тест: Вход с несуществующим email
     */
    @Test
    public void shouldReturn401WhenEmailNotFound() {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setEmail("nonexistent@test.com");
        loginDto.setPassword("anypass");

        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginDto);

        ResponseEntity<TokenResponseDto> response = httpClient.postForEntity(
                AUTH_RESOURCE + "/login", request, TokenResponseDto.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                "Response has unexpected status");
    }

    /**
     * Setup: Add users to database and create admin access token
     * Настройка: Добавление пользователей в БД и создание admin access токена
     */
    @BeforeEach
    public void setUp() {
        addUsersToDB();
        createAdminAccessToken();
    }

    /**
     * Create admin access token for tests that need authentication
     * Создание admin access токена для тестов, требующих аутентификации
     */
    private void createAdminAccessToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 60000); // 60 seconds / 60 секунд

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(accessPhrase));

        adminAccessToken = Jwts.builder()
                .subject("admin@test.com")
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Add test users to database
     * Добавление тестовых пользователей в БД
     */
    private void addUsersToDB() {
        // Create admin user
        // Создаем администратора
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("adminPass"));
        admin.setName("Admin");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setConfirmed(true);
        userRepository.save(admin);

        // Create regular user
        // Создаем обычного пользователя
        User regularUser = new User();
        regularUser.setEmail("user@test.com");
        regularUser.setPassword(passwordEncoder.encode("userPass"));
        regularUser.setName("Regular User");
        regularUser.setRole(Role.ROLE_USER);
        regularUser.setConfirmed(true);
        userRepository.save(regularUser);

        // Create unconfirmed user
        // Создаем неподтвержденного пользователя
        User unconfirmedUser = new User();
        unconfirmedUser.setEmail("unconfirmed@test.com");
        unconfirmedUser.setPassword(passwordEncoder.encode("unconfirmedPass"));
        unconfirmedUser.setName("Unconfirmed User");
        unconfirmedUser.setRole(Role.ROLE_USER);
        unconfirmedUser.setConfirmed(false);
        userRepository.save(unconfirmedUser);
    }

    /**
     * Clean database after each test
     * Очистка БД после каждого теста
     */
    @AfterEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    /**
     * Helper method to create authentication headers with token
     * Вспомогательный метод для создания заголовков аутентификации с токеном
     */
    private HttpHeaders createAuthHeaders(String token) {
        String tokenCookie = ACCESS_TOKEN_COOKIE_NAME + "=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, tokenCookie);
        return headers;
    }
}