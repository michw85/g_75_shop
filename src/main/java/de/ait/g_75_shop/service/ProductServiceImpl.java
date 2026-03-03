package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.dto.mapping.ProductMapper;
import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import de.ait.g_75_shop.dto.product.ProductUpdateDto;
import de.ait.g_75_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_75_shop.repository.ProductRepository;
import de.ait.g_75_shop.service.interfaces.FileService;
import de.ait.g_75_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;


/*
Что происходит при старте приложения:
1. Spring сканирует приложение и ищет интерфейс репозитория, который наследуется от JPA-репозиториев.
2. Data JPA генерирует класс нашего интерфейса репозитория с методами, в которых прописаны все нужные SQL-запросы в БД.
3. Spring создает объект этого класса (репозитория) и помещает его в Spring контекст.
4. Spring сканирует приложение и видит класс ProductServiceImpl, помеченный аннотацией @Service.
5. Spring создает объект этого класса используя конструктор public ProductServiceImpl(ProductRepository repository) - потому что другого нет
6.  Spring видит, что у конструктора есть входящий параметр ProductRepository repository.
7. Spring извлекает из Spring-контекста объект репозитория и передает его в конструктор.
8. Конструктор сохраняет объект репозитория в поле private final ProductRepository repository;
9. Мы в коде класса обращаемся к repository и вызываем его методы для доступа к БД
*/

/**
 * Implementation of ProductService interface
 * Provides business logic for product operations
 *
 * Реализация интерфейса ProductService
 * Предоставляет бизнес-логику для операций с товарами
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final FileService fileService;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, FileService fileService) {
        this.repository = repository;
        this.mapper = mapper;
        this.fileService = fileService;
    }

    /**
     * Saves a new product
     * Automatically sets product as active
     *
     * Сохраняет новый товар
     * Автоматически устанавливает товар как активный
     *
     * @param saveDto DTO with product data / DTO с данными товара
     * @return saved product as DTO / сохраненный товар в виде DTO
     */
    @Override
    public ProductDto save(ProductSaveDto saveDto) {
        Objects.requireNonNull(saveDto, "ProductSaveDto cannot be null");

        Product entity = mapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        repository.save(entity);

        // не всегда стоит логгировать объект целиком, ьак как он может быть очень большим
        logger.info("Product saved to the database: {}", entity);

        return mapper.mapEntityToDto(entity);
    }


    /**
     * Gets all active products
     *
     * Получает все активные товары
     *
     * @return list of active product DTOs / список DTO активных товаров
     */
    @Override
    public List<ProductDto> getAllActiveProducts() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .toList();
    }

    /**
     * Gets active product entity by ID
     *
     * Получает активный товар (сущность) по ID
     *
     * @param id product identifier / идентификатор товара
     * @return product entity / сущность товара
     * @throws EntityNotFoundException if product not found or inactive / если товар не найден или неактивен
     */
    @Override
    public Product getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null");
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(Product.class, id)
                );
    }

    /**
     * Gets active product DTO by ID
     *
     * Получает активный товар (DTO) по ID
     *
     * @param id product identifier / идентификатор товара
     * @return product DTO / DTO товара
     */
    @Override
    public ProductDto getActiveProductById(Long id) {
        Product product = getActiveEntityById(id);
        return mapper.mapEntityToDto(product);
    }

    /**
     * Updates product price
     *
     * Обновляет цену товара
     *
     * @param id product identifier / идентификатор товара
     * @param updateDto DTO with new price / DTO с новой ценой
     */
    @Override
    @Transactional
    public void update(Long id, ProductUpdateDto updateDto) {
        Objects.requireNonNull(id, "Product id cannot be null");
        Objects.requireNonNull(id, "ProductUpdateDto cannot be null");

        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, id))
                .setPrice(updateDto.getNewPrice());

        logger.info("Product id {} updated, new price : {}", id, updateDto.getNewPrice());
    }

    /**
     * Soft deletes product (deactivates)
     *
     * Мягкое удаление товара (деактивация)
     *
     * @param id product identifier / идентификатор товара
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        getActiveEntityById(id).setActive(false);
        logger.info("Product id {} marked as inactive", id);
//        repository.findByIdAndActiveTrue(id)
//                .ifPresent(x -> {
//                    x.setActive(false);
//                    logger.info("Product id {} marked as inactive", id);
//                });
    }

    /**
     * Restores previously deleted product (activates)
     *
     * Восстанавливает удаленный товар (активация)
     *
     * @param id product identifier / идентификатор товара
     */
    @Override
    @Transactional
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null");
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, id))
                .setActive(true);
        logger.info("Product id {} marked as active", id);
//                .ifPresent(x -> {
//                    x.setActive(true);
//                    logger.info("Product id {} marked as active", id);
//                });
    }

    /**
     * Gets count of active products
     *
     * Получает количество активных товаров
     *
     * @return count of active products / количество активных товаров
     */
    @Override
    public long getAllActiveProductsCount() {
        return repository.countByActiveTrue();
    }

    /**
     * Gets total cost of all active products
     *
     * Получает общую стоимость всех активных товаров
     *
     * @return total cost / общая стоимость
     */
    @Override
    public BigDecimal getAllActiveProductsTotalCost() {

        return repository.findAllByActiveTrue()
                .stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Gets average price of active products
     *
     * Получает среднюю цену активных товаров
     *
     * @return average price / средняя цена
     */
    @Override
    public BigDecimal getAllActiveProductsAveragePrice() {
        long productsCount = getAllActiveProductsCount();
        if (productsCount == 0) {
            return BigDecimal.ZERO;
        }
        return getAllActiveProductsTotalCost().divide(
                BigDecimal.valueOf(productsCount), 2, RoundingMode.HALF_UP);
    }

    /**
     * Checks if product is active
     *
     * Проверяет, активен ли товар
     *
     * @param id product identifier / идентификатор товара
     * @return true if active, false otherwise / true если активен, иначе false
     */
    @Override
    public boolean isProductActive(Long id) {
        return repository.existsByIdAndActiveTrue(id);
    }

    /**
     * Adds image to product
     *
     * Добавляет изображение к товару
     *
     * @param id product identifier / идентификатор товара
     * @param image image file to upload / файл изображения для загрузки
     * @throws IOException if file processing fails / если ошибка обработки файла
     */
    @Override
    @Transactional
    public void addImage(Long id, MultipartFile image) throws IOException {
        Objects.requireNonNull(id, "Product id cannot be null");

        Product product = getActiveEntityById(id);
        // Upload file and get URL / Загружаем файл и получаем ссылку
        String imageUrl = fileService.uploadAndGetUrl(image);
        // Set image URL to product / Присваиваем ссылку товару
        product.setImageUrl(imageUrl);
    }
}
// RoundingMode.HALF_UP - способы округления остатка