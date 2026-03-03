package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.Cart;
import de.ait.g_75_shop.domain.Customer;
import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.dto.customer.CustomerDto;
import de.ait.g_75_shop.dto.customer.CustomerSaveDto;
import de.ait.g_75_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_75_shop.dto.mapping.CustomerMapper;
import de.ait.g_75_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_75_shop.exceptions.types.EntityUpdateException;
import de.ait.g_75_shop.repository.CustomerRepository;
import de.ait.g_75_shop.service.interfaces.CustomerService;
import de.ait.g_75_shop.service.interfaces.FileService;
import de.ait.g_75_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerService interface
 * Provides business logic for customer operations
 *
 * Реализация интерфейса CustomerService
 * Предоставляет бизнес-логику для операций с покупателями
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final ProductService productService;
    private final CustomerMapper mapper;
    private final FileService fileService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ProductService productService,
                               CustomerMapper mapper, FileService fileService) {
        this.customerRepository = customerRepository;
        this.productService = productService;
        this.mapper = mapper;
        this.fileService = fileService;
    }

    /**
     * Saves a new customer
     * Creates customer and automatically creates an empty cart for them
     *
     * Сохраняет нового покупателя
     * Создает покупателя и автоматически создает для него пустую корзину
     *
     * @param saveDto DTO with customer data / DTO с данными покупателя
     * @return saved customer as DTO / сохраненный покупатель в виде DTO
     * @throws NullPointerException if saveDto is null / если saveDto null
     */
    @Override
    public CustomerDto save(CustomerSaveDto saveDto) {
        // Null check / Проверка на null
        Objects.requireNonNull(saveDto, "CustomerSaveDto cannot be null");

        // Convert DTO to entity / Преобразуем DTO в сущность
        Customer customer = mapper.mapDtoToEntity(saveDto);

        // Set customer as active / Устанавливаем покупателя как активного
        customer.setActive(true);

        // Save customer / Сохраняем покупателя
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer saved with ID: {}", savedCustomer.getId());

        // Create and link cart / Создаем и связываем корзину
        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        savedCustomer.setCart(cart);

        // Save customer with cart / Сохраняем покупателя с корзиной
        savedCustomer = customerRepository.save(savedCustomer);
        logger.info("Cart created for customer ID: {}", savedCustomer.getId());

        // Return DTO / Возвращаем DTO
        return mapper.mapEntityToDto(savedCustomer);
    }

    /**
     * Gets all active customers
     *
     * Получает всех активных покупателей
     *
     * @return list of active customer DTOs / список DTO активных покупателей
     */
    @Override
    public List<CustomerDto> getAllActiveCustomers() {
        return customerRepository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets active customer entity by ID
     *
     * Получает активного покупателя (сущность) по ID
     *
     * @param id customer identifier / идентификатор покупателя
     * @return customer entity / сущность покупателя
     * @throws NullPointerException if id is null / если id null
     * @throws EntityNotFoundException if customer not found or inactive / если покупатель не найден или неактивен
     */
    @Override
    public Customer getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Customer ID cannot be null");
        return customerRepository.findByIdAndActiveTrue(id)
                .orElseThrow(
                        () -> {
                            logger.warn("Active customer with ID {} not found", id);
                            return new EntityNotFoundException(Customer.class, id);
                        }
                );
    }

    /**
            * Gets active customer DTO by ID
     *
             * Получает активного покупателя (DTO) по ID
     *
             * @param id customer identifier / идентификатор покупателя
     * @return customer DTO / DTO покупателя
     */
    @Override
    public CustomerDto getActiveCustomerById(Long id) {
//        Customer customer = getActiveEntityById(id);
//        return customer != null ? mapper.mapEntityToDto(customer) : null;
        Customer customer = getActiveEntityById(id);
        return mapper.mapEntityToDto(customer);
    }

    /**
     * Updates customer name
     *
     * Обновляет имя покупателя
     *
     * @param id customer identifier / идентификатор покупателя
     * @param updateDto DTO with new name / DTO с новым именем
     * @throws NullPointerException if parameters are null / если параметры null
     * @throws EntityNotFoundException if customer not found / если покупатель не найден
     */
    @Override
    @Transactional
    public void update(Long id, @Valid CustomerUpdateDto updateDto) {
//        customerRepository.findByIdAndActiveTrue(id)
//                .ifPresent(existingCustomer -> {
//                    mapper.updateEntity(existingCustomer, updateDto);
//                });

        // Null checks / Проверка на null
        Objects.requireNonNull(id, "Customer ID cannot be null");
        Objects.requireNonNull(updateDto, "CustomerUpdateDto cannot be null");

        // Get existing customer / Получаем существующего покупателя
        Customer existingCustomer = getActiveEntityById(id);

        // Update name / Обновляем имя
        existingCustomer.setName(updateDto.getNewName());

        logger.info("Customer ID {} updated. New name: '{}'", id, updateDto.getNewName());
    }

    /**
     * Soft deletes customer (deactivates)
     *
     * Мягкое удаление покупателя (деактивация)
     *
     * @param id customer identifier / идентификатор покупателя
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        Customer customer = getActiveEntityById(id);
        customer.setActive(false);

        logger.info("Customer ID {} marked as inactive", id);
//        if (customer != null) {
//            customer.setActive(false);
//        }

    }

    /**
     * Restores previously deleted customer (activates)
     *
     * Восстанавливает удаленного покупателя (активация)
     *
     * @param id customer identifier / идентификатор покупателя
     */
    @Override
    @Transactional
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Customer ID cannot be null");

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Customer.class, id));

        customer.setActive(true);
        logger.info("Customer ID {} restored (marked as active)", id);
//        customerRepository.findById(id)
//                .ifPresent(customer -> {
//                    customer.setActive(true);
//                });
    }

    /**
     * Gets count of active customers
     *
     * Получает количество активных покупателей
     *
     * @return count of active customers / количество активных покупателей
     */
    @Override
    public long getAllActiveCustomersCount() {
        return customerRepository.countByActiveTrue();
    }

    /**
     * Gets total cost of customer's cart
     *
     * Получает общую стоимость корзины покупателя
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @return total cart cost / общая стоимость корзины
     */
    @Override
    public BigDecimal getCustomerCartTotalCost(Long customerId) {
        Customer customer = getActiveEntityById(customerId);
        if (customer == null || customer.getCart() == null) {
            return BigDecimal.ZERO;
        }
        return customer.getCart().getTotalPrice();
    }

    /**
     * Gets average price in customer's cart
     *
     * Получает среднюю цену товаров в корзине покупателя
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @return average price / средняя цена
     */
    @Override
    public BigDecimal getCustomerCartAveragePrice(Long customerId) {
        Customer customer = getActiveEntityById(customerId);
        if (customer == null || customer.getCart() == null) {
            return BigDecimal.ZERO;
        }
        return customer.getCart().getAveragePrice();
    }

    /**
     * Adds product to customer's cart with validation
     *
     * Добавляет товар в корзину покупателя с валидацией
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @param productId product identifier / идентификатор товара
     * @param quantity quantity to add / количество для добавления
     * @throws EntityUpdateException if product inactive, quantity invalid, or cart limits exceeded
     *                               если товар неактивен, количество некорректно или превышены лимиты корзины
     */
    @Override
    @Transactional
    public void addProductToCart(Long customerId, Long productId, int quantity) {
        // Null checks / Проверка параметров на null
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(productId, "Product ID cannot be null");

        // Получаем активный товар через сервис продуктов
//        Product product = productService.getActiveEntityById(productId);
//        if (product == null) {
//            System.out.println("Товар с ID " + productId + " не найден или не активен");
//            return;
//        }

        // Validate quantity / Проверяем количество
        if (quantity <= 0) {
            throw new EntityUpdateException(
                    String.format("Quantity must be positive. Provided: %d", quantity));
        }

        if (quantity > 100) {
            throw new EntityUpdateException(
                    String.format("Cannot add more than 100 items. Provided: %d", quantity)
            );
        }

        // Get customer / Проверяем существование активного покупателя
        Customer customer = getActiveEntityById(customerId);
        if (customer == null) {
            System.out.println("Покупатель с ID " + customerId + " не найден или не активен");
            return;
        }

        // Get product (check if active) / Получаем товар (проверяем, что он активен)
        Product product;
        try {
            product = productService.getActiveEntityById(productId);
        } catch (EntityNotFoundException e) {
            throw new EntityUpdateException(
                    String.format("Cannot add product to cart: Product with ID %d is not active or does not exist",
                            productId), e
            );
        }

        // Get or create cart / Получаем или создаем корзину
        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
            logger.debug("New cart created for customer ID: {}", customerId);
        }

        // Check cart size limit / Проверяем, не превышен ли лимит позиций в корзине
        if (cart.getPositions().size() >= 50) {
            throw new EntityUpdateException(
                    "Cart cannot contain more than 50 different items"
            );
        }

        // Add product to cart / Добавляем товар в корзину
        cart.addPosition(product, quantity);
        logger.info("Product ID {} (quantity: {}) added to cart of customer ID {}",
                productId, quantity, customerId);

    }

    /**
     * Removes product from customer's cart
     *
     * Удаляет товар из корзины покупателя
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @param productId product identifier / идентификатор товара
     * @throws EntityUpdateException if cart empty or product not found
     *                               если корзина пуста или товар не найден
     */
    @Override
    @Transactional
    public void removeProductFromCart(Long customerId, Long productId) {
        // Null checks / Проверка параметров на null
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(productId, "Product ID cannot be null");

        // Get customer / Получаем покупателя
        Customer customer = getActiveEntityById(customerId);

        // Check if cart exists and not empty / Проверяем наличие корзины
        if (customer == null || customer.getCart().getPositions().isEmpty()) {
            throw new EntityUpdateException(
                    String.format("Cannot remove product: Cart of customer ID %d is empty", customerId)
            );
        }

        Cart cart = customer.getCart();

        // Check if product exists in cart / Проверяем, есть ли такой товар в корзине
        boolean productExists = cart.getPositions().stream()
                .anyMatch(p -> p.getProduct().getId().equals(productId));

        if (!productExists) {
            throw new EntityUpdateException(
                    String.format("Product ID %d not found in cart of customer ID %d",
                            productId, customerId)
            );
        }

        // Remove product from cart / Удаляем товар из корзины
        cart.removePositionById(productId);
        logger.info("Product ID {} removed from cart of customer ID {}", productId, customerId);
    }

    /**
     * Clears customer's cart completely
     *
     * Полностью очищает корзину покупателя
     *
     * @param customerId customer identifier / идентификатор покупателя
     * @throws EntityUpdateException if cart already empty / если корзина уже пуста
     */
    @Override
    @Transactional
    public void clearCart(Long customerId) {
        // Null check / Проверка параметров на null
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        // Get customer / Получаем покупателя
        Customer customer = getActiveEntityById(customerId);

        // Check if cart exists and not empty / Проверяем наличие корзины
        if (customer.getCart() == null || customer.getCart().getPositions().isEmpty()) {
            throw new EntityUpdateException(
                    String.format("Cannot clear cart: Cart of customer ID %d is already empty", customerId)
            );
        }

        // Clear cart / Очищаем корзину
        customer.getCart().clearCart();
        logger.info("Cart of customer ID {} cleared", customerId);
    }

    /**
     * Adds image to customer profile
     *
     * Добавляет изображение в профиль покупателя
     *
     * @param id customer identifier / идентификатор покупателя
     * @param image image file to upload / файл изображения для загрузки
     * @throws IOException if file processing fails / если ошибка обработки файла
     */
    @Override
    @Transactional
    public void addImage(Long id, MultipartFile image) throws IOException {
        Objects.requireNonNull(id, "Product id cannot be null");

        Customer customer = getActiveEntityById(id);
        // Upload file and get URL / обращение к сервису файлов. Загружаем файл и получение ссылки на файл
        String imageUrl = fileService.uploadAndGetUrl(image);
        //Set image URL to customer / присвоение этой ссылки покупателю
        customer.setImageUrl(imageUrl);
    }
}