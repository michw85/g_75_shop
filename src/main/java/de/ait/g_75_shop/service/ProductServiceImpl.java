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

    //    @Override
//    public Product save(Product product) {
//        product.setActive(true);
//        return repository.save(product);
//    }
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

    @Override
    public List<ProductDto> getAllActiveProducts() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .toList();
    }

    @Override
    public Product getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null");
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(Product.class, id)
                );
    }

    @Override
    public ProductDto getActiveProductById(Long id) {
        Product product = getActiveEntityById(id);
        return mapper.mapEntityToDto(product);
    }

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

    @Override
    public long getAllActiveProductsCount() {
        return repository.countByActiveTrue();
    }

    @Override
    public BigDecimal getAllActiveProductsTotalCost() {

        return repository.findAllByActiveTrue()
                .stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getAllActiveProductsAveragePrice() {
        long productsCount = getAllActiveProductsCount();
        if (productsCount == 0) {
            return BigDecimal.ZERO;
        }
        return getAllActiveProductsTotalCost().divide(
                BigDecimal.valueOf(productsCount), 2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isProductActive(Long id) {
        return repository.existsByIdAndActiveTrue(id);
    }

    @Override
    @Transactional
    public void addImage(Long id, MultipartFile image) throws IOException {
        Objects.requireNonNull(id, "Product id cannot be null");

        Product product = getActiveEntityById(id);
        // Здесь будет обращение к сервису файлов и получение ссылки на файл
        String imageUrl = fileService.uploadAndGetUrl(image);
        // Здесь будет присвоение этой ссылки найденному продукту
        product.setImageUrl(imageUrl);
    }
}
// RoundingMode.HALF_UP - способы округления остатка