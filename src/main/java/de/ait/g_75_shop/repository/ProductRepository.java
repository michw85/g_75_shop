package de.ait.g_75_shop.repository;

import de.ait.g_75_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for Product entity operations
 *
 * How Spring Data JPA works:
 * 1. When application starts, technology scans our Product class and its fields
 * 2. Knowing the class and fields, it "understands" how to work with them in DB
 * 3. It automatically writes a class that implements our ProductRepository interface
 * 4. In this class, it writes standard methods for DB operations like findAll, findById, save...
 *    And in these methods, it writes the necessary SQL queries to the database
 * 5. Spring creates an object of this class (written by JPA technology) and puts it in Spring Context
 *
 * Интерфейс репозитория для операций с сущностью Product
 *
 * Как работает технология Spring Data JPA:
 * 1. При старте приложения технология сканирует наш класс Product и его поля
 * 2. Зная класс и поля, она "понимает", как с этим работать в БД
 * 3. Она сама пишет класс, который реализует наш интерфейс ProductRepository
 * 4. В этом классе она сама пишет стандартные методы по работе с БД, например findAll, findById, save...
 *    И в этих методах она сама прописывает нужные SQL запросы в БД
 * 5. Spring создаёт объект этого класса, который написала технология JPA, и помещает его в Spring Context
 */

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Finds all active products
     * Находит все активные товары
     *
     * @return list of active products / список активных товаров
     */
    List<Product> findAllByActiveTrue();
    /**
     * Finds active product by ID
     * Находит активный товар по ID
     *
     * @param id product identifier / идентификатор товара
     * @return Optional containing product if found and active / Optional с товаром, если найден и активен
     */
    Optional<Product> findByIdAndActiveTrue(Long id);
    /**
     * Counts all active products
     * Подсчитывает все активные товары
     *
     * @return count of active products / количество активных товаров
     */
    long countByActiveTrue();
    /**
     * Checks if active product exists with given ID
     * Проверяет существование активного товара с указанным ID
     *
     * @param id product identifier / идентификатор товара
     * @return true if exists and active / true если существует и активен
     */
    boolean existsByIdAndActiveTrue(Long id);
}
