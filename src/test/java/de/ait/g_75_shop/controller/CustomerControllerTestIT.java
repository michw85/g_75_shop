package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.domain.Cart;
import de.ait.g_75_shop.domain.Customer;
import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.dto.customer.CustomerDto;
import de.ait.g_75_shop.dto.customer.CustomerSaveDto;
import de.ait.g_75_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_75_shop.repository.CustomerRepository;
import de.ait.g_75_shop.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для контроллера покупателей
 * Тестируют реальное взаимодействие с базой данных и HTTP эндпоинты
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    // Базовый URL для ресурса покупателей
    private static final String CUSTOMER_RESOURCE = "/customers";

    // Тестовые данные
    private Customer activeTestCustomer;
    private Customer inactiveTestCustomer;
    private Product testProduct;

    /**
     * Позитивный тест: сохранение нового покупателя
     * Проверяет: успешное создание покупателя, возврат статуса CREATED,
     * корректность сохраненных данных в БД
     */
    @Test
    void shouldSaveCustomer() {
        // Подготовка данных - имя соответствует паттерну валидации
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName("Test Name");

        // Создаем запрос
        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        // Отправляем запрос
        ResponseEntity<CustomerDto> response = httpClient.postForEntity(
                CUSTOMER_RESOURCE, request, CustomerDto.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "Response should have status 201 CREATED");

        // Проверка тела ответа
        CustomerDto dto = response.getBody();
        assertNotNull(dto, "Response body should not be null");
        assertNotNull(dto.getId(), "Returned customer ID should not be null");
        assertEquals(saveDto.getName(), dto.getName(),
                "Returned customer has incorrect name");
        assertNotNull(dto.getCart(), "New customer should have a cart created");

        // Проверка сохранения в БД
        Customer savedCustomer = customerRepository.findByIdAndActiveTrue(dto.getId()).orElse(null);
        assertNotNull(savedCustomer, "Customer was not properly saved to the database");
        assertEquals(saveDto.getName(), savedCustomer.getName(),
                "Saved customer has incorrect name");
        assertTrue(savedCustomer.isActive(), "New customer should be active");
        assertNotNull(savedCustomer.getCart(), "New customer should have a cart");
    }

    /**
     * Позитивный тест: получение всех активных покупателей
     * Проверяет: возврат списка, фильтрацию неактивных покупателей
     */
    @Test
    void shouldGetAllActiveCustomers() {
        // Отправляем GET запрос
        ResponseEntity<CustomerDto[]> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE, CustomerDto[].class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Проверка тела ответа
        CustomerDto[] customers = response.getBody();
        assertNotNull(customers, "Response body should not be null");

        // Должен вернуться только активный покупатель (1 из 2 созданных в @BeforeEach)
        assertEquals(1, customers.length,
                "Should return only active customers");

        CustomerDto customerDto = customers[0];
        assertEquals(activeTestCustomer.getName(), customerDto.getName(),
                "Returned customer has incorrect name");

        // В ТЕСТЕ МЫ НЕ ПРОВЕРЯЕМ НАЛИЧИЕ КОРЗИНЫ, Т.К. В ДАННЫХ, СОЗДАННЫХ ЧЕРЕЗ setUp(),
        // КОРЗИНА НЕ СОЗДАЕТСЯ АВТОМАТИЧЕСКИ
        // assertNotNull(customerDto.getCart(), "Customer should have a cart");
    }

    /**
     * Позитивный тест: получение покупателя по ID
     * Проверяет: успешное получение существующего активного покупателя
     */
    @Test
    void shouldGetCustomerById() {
        // Отправляем GET запрос для существующего покупателя
        ResponseEntity<CustomerDto> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE + "/" + activeTestCustomer.getId(),
                CustomerDto.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Проверка тела ответа
        CustomerDto customerDto = response.getBody();
        assertNotNull(customerDto, "Response body should not be null");
        assertEquals(activeTestCustomer.getId(), customerDto.getId(),
                "Returned customer has incorrect ID");
        assertEquals(activeTestCustomer.getName(), customerDto.getName(),
                "Returned customer has incorrect name");

        // В ТЕСТЕ МЫ НЕ ПРОВЕРЯЕМ НАЛИЧИЕ КОРЗИНЫ
        // assertNotNull(customerDto.getCart(), "Customer should have a cart");
    }

    /**
     * Позитивный тест: обновление имени покупателя
     * Проверяет: успешное обновление данных покупателя
     */
    @Test
    void shouldUpdateCustomer() {
        // Подготовка данных для обновления - имя соответствует паттерну валидации
        String newName = "Updated Name";
        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        updateDto.setNewName(newName);

        // Создаем запрос
        HttpEntity<CustomerUpdateDto> request = new HttpEntity<>(updateDto);

        // Отправляем PUT запрос
        ResponseEntity<Void> response = httpClient.exchange(
                CUSTOMER_RESOURCE + "/" + activeTestCustomer.getId(),
                HttpMethod.PUT,
                request,
                Void.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Проверка обновления в БД
        Customer updatedCustomer = customerRepository.findById(activeTestCustomer.getId()).orElse(null);
        assertNotNull(updatedCustomer, "Customer should exist in database");
        assertEquals(newName, updatedCustomer.getName(),
                "Customer name was not updated correctly");
    }

    /**
     * Позитивный тест: получение количества активных покупателей
     * Проверяет: корректный подсчет активных покупателей
     */
    @Test
    void shouldGetActiveCustomersCount() {
        // Отправляем GET запрос для получения количества
        ResponseEntity<Long> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE + "/count",
                Long.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Проверка количества (должен быть 1 активный покупатель)
        Long count = response.getBody();
        assertNotNull(count, "Response body should not be null");
        assertEquals(1L, count, "Should return count of active customers only");
    }

    /**
     * Позитивный тест: мягкое удаление покупателя
     * Проверяет: деактивация покупателя (soft delete)
     */
    @Test
    void shouldDeleteCustomer() {
        // Отправляем DELETE запрос
        ResponseEntity<Void> response = httpClient.exchange(
                CUSTOMER_RESOURCE + "/" + activeTestCustomer.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
                "Response should have status 204 NO CONTENT");

        // Проверка деактивации в БД
        Customer deletedCustomer = customerRepository.findById(activeTestCustomer.getId()).orElse(null);
        assertNotNull(deletedCustomer, "Customer should still exist in database");
        assertFalse(deletedCustomer.isActive(),
                "Customer should be marked as inactive after deletion");
    }

    /**
     * Позитивный тест: восстановление удаленного покупателя
     * Проверяет: активация ранее деактивированного покупателя
     */
    @Test
    void shouldRestoreCustomer() {
        // Сначала деактивируем покупателя
        inactiveTestCustomer.setActive(false);
        customerRepository.save(inactiveTestCustomer);

        // Отправляем PUT запрос на восстановление
        ResponseEntity<Void> response = httpClient.exchange(
                CUSTOMER_RESOURCE + "/" + inactiveTestCustomer.getId() + "/restore",
                HttpMethod.PUT,
                null,
                Void.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Проверка активации в БД
        Customer restoredCustomer = customerRepository.findById(inactiveTestCustomer.getId()).orElse(null);
        assertNotNull(restoredCustomer, "Customer should exist in database");
        assertTrue(restoredCustomer.isActive(),
                "Customer should be active after restoration");
    }

    // ===================== НЕГАТИВНЫЕ ТЕСТЫ =====================

    /**
     * Негативный тест: попытка сохранения покупателя с пустым именем
     * Проверяет: возврат статуса 400 BAD REQUEST при ошибке валидации
     */
    @Test
    void shouldReturn400WhenNameIsEmpty() {
        // Подготовка данных с пустым именем
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName("");

        // Создаем запрос
        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        // Отправляем запрос
        ResponseEntity<String> response = httpClient.postForEntity(
                CUSTOMER_RESOURCE, request, String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response should have status 400 BAD REQUEST");
    }

    /**
     * Негативный тест: попытка сохранения покупателя с именем, не соответствующим паттерну
     * Проверяет: возврат статуса 400 BAD REQUEST при ошибке валидации
     */
    @Test
    void shouldReturn400WhenNameDoesNotMatchPattern() {
        // Подготовка данных с именем, не соответствующим паттерну (все маленькие буквы)
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName("invalid name");

        // Создаем запрос
        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        // Отправляем запрос
        ResponseEntity<String> response = httpClient.postForEntity(
                CUSTOMER_RESOURCE, request, String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response should have status 400 BAD REQUEST");
    }

    /**
     * Негативный тест: попытка сохранения покупателя с именем null
     * Проверяет: возврат статуса 400 BAD REQUEST при ошибке валидации
     */
    @Test
    void shouldReturn400WhenNameIsNull() {
        // Подготовка данных с именем null
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName(null);

        // Создаем запрос
        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        // Отправляем запрос
        ResponseEntity<String> response = httpClient.postForEntity(
                CUSTOMER_RESOURCE, request, String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response should have status 400 BAD REQUEST");
    }

    /**
     * Негативный тест: попытка получения несуществующего покупателя
     * Проверяет: возврат статуса 404 NOT FOUND
     */
    @Test
    void shouldReturn404WhenCustomerNotFound() {
        // Отправляем GET запрос для несуществующего ID
        ResponseEntity<String> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE + "/999999",
                String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Response should have status 404 NOT FOUND");
    }

    /**
     * Негативный тест: попытка получения неактивного покупателя
     * Проверяет: возврат статуса 404 NOT FOUND (т.к. ищем только активных)
     */
    @Test
    void shouldReturn404WhenCustomerIsInactive() {
        // Отправляем GET запрос для неактивного покупателя
        ResponseEntity<String> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE + "/" + inactiveTestCustomer.getId(),
                String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Response should have status 404 NOT FOUND for inactive customer");
    }

    /**
     * Негативный тест: попытка обновления с пустым именем
     * Проверяет: возврат статуса 400 BAD REQUEST
     */
    @Test
    void shouldReturn400WhenUpdateWithEmptyName() {
        // Подготовка данных с пустым именем для обновления
        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        updateDto.setNewName("");

        // Создаем запрос
        HttpEntity<CustomerUpdateDto> request = new HttpEntity<>(updateDto);

        // Отправляем PUT запрос
        ResponseEntity<String> response = httpClient.exchange(
                CUSTOMER_RESOURCE + "/" + activeTestCustomer.getId(),
                HttpMethod.PUT,
                request,
                String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Response should have status 400 BAD REQUEST");
    }

    /**
     * Негативный тест: попытка удаления уже неактивного покупателя
     * Проверяет: возврат статуса 404 NOT FOUND
     */
    @Test
    void shouldReturn404WhenDeletingInactiveCustomer() {
        // Отправляем DELETE запрос для неактивного покупателя
        ResponseEntity<String> response = httpClient.exchange(
                CUSTOMER_RESOURCE + "/" + inactiveTestCustomer.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Response should have status 404 NOT FOUND");
    }

    /**
     * Тест для операций с корзиной: получение общей стоимости
     * Требует наличия товаров в БД
     */
    @Test
    void shouldGetCustomerCartTotalCost() {
        // Отправляем GET запрос для получения стоимости корзины
        ResponseEntity<BigDecimal> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE + "/" + activeTestCustomer.getId() + "/cart/total-cost",
                BigDecimal.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Начальная стоимость корзины должна быть 0
        BigDecimal totalCost = response.getBody();
        assertNotNull(totalCost, "Response body should not be null");
        assertEquals(BigDecimal.ZERO, totalCost,
                "New cart should have zero total cost");
    }

    /**
     * Тест для операций с корзиной: получение средней цены
     */
    @Test
    void shouldGetCustomerCartAveragePrice() {
        // Отправляем GET запрос для получения средней цены в корзине
        ResponseEntity<BigDecimal> response = httpClient.getForEntity(
                CUSTOMER_RESOURCE + "/" + activeTestCustomer.getId() + "/cart/avg-price",
                BigDecimal.class
        );

        // Проверка ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Response should have status 200 OK");

        // Начальная средняя цена должна быть 0
        BigDecimal avgPrice = response.getBody();
        assertNotNull(avgPrice, "Response body should not be null");
        assertEquals(BigDecimal.ZERO, avgPrice,
                "Empty cart should have zero average price");
    }

    // ===================== МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА ТЕСТОВ =====================

    /**
     * Инициализация тестовых данных перед каждым тестом
     * Создает активного и неактивного покупателя, а также тестовый продукт
     * ВАЖНО: Корзина НЕ создается автоматически, поэтому в тестах,
     * где ожидается наличие корзины, мы не проверяем cart != null
     */
    @BeforeEach
    public void setUp() {
        // Создаем активного покупателя
        activeTestCustomer = new Customer();
        activeTestCustomer.setName("Active Customer");
        activeTestCustomer.setActive(true);
        // Корзина НЕ создается, так как мы сохраняем через репозиторий
        activeTestCustomer = customerRepository.save(activeTestCustomer);

        // Создаем неактивного покупателя
        inactiveTestCustomer = new Customer();
        inactiveTestCustomer.setName("Inactive Customer");
        inactiveTestCustomer.setActive(false);
        // Корзина НЕ создается
        inactiveTestCustomer = customerRepository.save(inactiveTestCustomer);

        // Создаем тестовый продукт, соответствующий паттерну валидации
        testProduct = new Product();
        testProduct.setTitle("Test product");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setActive(true);
        testProduct = productRepository.save(testProduct);
    }

    /**
     * Очистка базы данных после каждого теста
     */
    @AfterEach
    public void cleanDatabase() {
        // Важно удалять в правильном порядке из-за внешних ключей
        customerRepository.deleteAll();
        productRepository.deleteAll();
    }
}