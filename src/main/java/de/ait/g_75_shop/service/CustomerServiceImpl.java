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
import de.ait.g_75_shop.service.CustomerService;
import de.ait.g_75_shop.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final ProductService productService;
    private final CustomerMapper mapper;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ProductService productService,
                               CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.productService = productService;
        this.mapper = mapper;
    }

    /*
     * Сохранение нового покупателя
     * @param saveDto DTO с данными покупателя
     * @return сохраненный покупатель в виде DTO
     * @throws NullPointerException если saveDto null
     */
    @Override
    public CustomerDto save(CustomerSaveDto saveDto) {
        // Проверка на null
        Objects.requireNonNull(saveDto, "CustomerSaveDto cannot be null");

        // Преобразуем DTO в сущность
        Customer customer = mapper.mapDtoToEntity(saveDto);

        // Устанавливаем покупателя как активного
        customer.setActive(true);

        // Сохраняем покупателя
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer saved with ID: {}", savedCustomer.getId());

        // Создаем и связываем корзину
        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        savedCustomer.setCart(cart);

        // Сохраняем покупателя с корзиной
        savedCustomer = customerRepository.save(savedCustomer);
        logger.info("Cart created for customer ID: {}", savedCustomer.getId());

        // Возвращаем DTO
        return mapper.mapEntityToDto(savedCustomer);
    }

    /**
     * Получение всех активных покупателей
     *
     * @return список DTO активных покупателей
     */
    @Override
    public List<CustomerDto> getAllActiveCustomers() {
        return customerRepository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение активного покупателя по ID (сущность)
     *
     * @param id идентификатор покупателя
     * @return сущность покупателя
     * @throws NullPointerException    если id null
     * @throws EntityNotFoundException если покупатель не найден или неактивен
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
     * Получение активного покупателя по ID (DTO)
     *
     * @param id идентификатор покупателя
     * @return DTO покупателя
     */
    @Override
    public CustomerDto getActiveCustomerById(Long id) {
//        Customer customer = getActiveEntityById(id);
//        return customer != null ? mapper.mapEntityToDto(customer) : null;
        Customer customer = getActiveEntityById(id);
        return mapper.mapEntityToDto(customer);
    }

    /**
     * Обновление имени покупателя
     *
     * @param id        идентификатор покупателя
     * @param updateDto DTO с новым именем
     * @throws NullPointerException    если параметры null
     * @throws EntityNotFoundException если покупатель не найден
     */
    @Override
    @Transactional
    public void update(Long id, @Valid CustomerUpdateDto updateDto) {
//        customerRepository.findByIdAndActiveTrue(id)
//                .ifPresent(existingCustomer -> {
//                    mapper.updateEntity(existingCustomer, updateDto);
//                });

        // Проверка на null
        Objects.requireNonNull(id, "Customer ID cannot be null");
        Objects.requireNonNull(updateDto, "CustomerUpdateDto cannot be null");

        // Получаем существующего покупателя
        Customer existingCustomer = getActiveEntityById(id);

        // Обновляем имя
        existingCustomer.setName(updateDto.getNewName());

        logger.info("Customer ID {} updated. New name: '{}'", id, updateDto.getNewName());
    }

    /**
     * Мягкое удаление покупателя (деактивация)
     *
     * @param id идентификатор покупателя
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
     * Восстановление удаленного покупателя (активация)
     *
     * @param id идентификатор покупателя
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
     * Получение количества активных покупателей
     *
     * @return количество активных покупателей
     */
    @Override
    public long getAllActiveCustomersCount() {
        return customerRepository.countByActiveTrue();
    }

    /**
     * Получение общей стоимости корзины покупателя
     *
     * @param customerId идентификатор покупателя
     * @return общая стоимость корзины
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
     * Получение средней цены товаров в корзине покупателя
     *
     * @param customerId идентификатор покупателя
     * @return средняя цена
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
     * Добавление товара в корзину покупателя
     *
     * @param customerId идентификатор покупателя
     * @param productId  идентификатор товара
     * @param quantity   количество товара
     * @throws EntityUpdateException если товар неактивен или количество некорректно
     */
    @Override
    @Transactional
    public void addProductToCart(Long customerId, Long productId, int quantity) {
        // Проверка параметров на null
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(productId, "Product ID cannot be null");

        // Получаем активный товар через сервис продуктов
//        Product product = productService.getActiveEntityById(productId);
//        if (product == null) {
//            System.out.println("Товар с ID " + productId + " не найден или не активен");
//            return;
//        }

        // Проверяем количество
        if (quantity <= 0) {
            throw new EntityUpdateException(
                    String.format("Quantity must be positive. Provided: %d", quantity));
        }

        if (quantity > 100) {
            throw new EntityUpdateException(
                    String.format("Cannot add more than 100 items. Provided: %d", quantity)
            );
        }

        // Проверяем существование активного покупателя
        Customer customer = getActiveEntityById(customerId);
        if (customer == null) {
            System.out.println("Покупатель с ID " + customerId + " не найден или не активен");
            return;
        }

        // Получаем товар (проверяем, что он активен)
        Product product;
        try {
            product = productService.getActiveEntityById(productId);
        } catch (EntityNotFoundException e) {
            throw new EntityUpdateException(
                    String.format("Cannot add product to cart: Product with ID %d is not active or does not exist",
                            productId), e
            );
        }

        // Получаем или создаем корзину
        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
            logger.debug("New cart created for customer ID: {}", customerId);
        }

        // Проверяем, не превышен ли лимит позиций в корзине
        if (cart.getPositions().size() >= 50) {
            throw new EntityUpdateException(
                    "Cart cannot contain more than 50 different items"
            );
        }

        // Добавляем товар в корзину
        cart.addPosition(product, quantity);
        logger.info("Product ID {} (quantity: {}) added to cart of customer ID {}",
                productId, quantity, customerId);

    }

    /**
     * Удаление товара из корзины покупателя
     * @param customerId идентификатор покупателя
     * @param productId идентификатор товара
     * @throws EntityUpdateException если корзина пуста или товар не найден
     */
    @Override
    @Transactional
    public void removeProductFromCart(Long customerId, Long productId) {
        // Проверка параметров на null
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(productId, "Product ID cannot be null");

        // Получаем покупателя
        Customer customer = getActiveEntityById(customerId);

        // Проверяем наличие корзины
        if (customer == null || customer.getCart().getPositions().isEmpty()) {
            throw new EntityUpdateException(
                    String.format("Cannot remove product: Cart of customer ID %d is empty", customerId)
            );
        }

        Cart cart = customer.getCart();

        // Проверяем, есть ли такой товар в корзине
        boolean productExists = cart.getPositions().stream()
                .anyMatch(p -> p.getProduct().getId().equals(productId));

        if (!productExists) {
            throw new EntityUpdateException(
                    String.format("Product ID %d not found in cart of customer ID %d",
                            productId, customerId)
            );
        }

        // Удаляем товар из корзины
        cart.removePositionById(productId);
        logger.info("Product ID {} removed from cart of customer ID {}", productId, customerId);
    }

    /**
     * Очистка корзины покупателя
     * @param customerId идентификатор покупателя
     * @throws EntityUpdateException если корзина уже пуста
     */
    @Override
    @Transactional
    public void clearCart(Long customerId) {
        // Проверка параметров на null
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        // Получаем покупателя
        Customer customer = getActiveEntityById(customerId);

        // Проверяем наличие корзины
        if (customer.getCart() == null || customer.getCart().getPositions().isEmpty()) {
            throw new EntityUpdateException(
                    String.format("Cannot clear cart: Cart of customer ID %d is already empty", customerId)
            );
        }

        // Очищаем корзину
        customer.getCart().clearCart();
        logger.info("Cart of customer ID {} cleared", customerId);
    }
}