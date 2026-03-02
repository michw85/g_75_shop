package de.ait.g_75_shop.repository;

import de.ait.g_75_shop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations
 * Extends JpaRepository to get basic CRUD functionality
 *
 * Интерфейс репозитория для операций с сущностью Customer
 * Расширяет JpaRepository для получения базового CRUD функционала
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds all active customers
     * Находит всех активных покупателей
     *
     * @return list of active customers / список активных покупателей
     */
    List<Customer> findAllByActiveTrue();

    /**
     * Finds active customer by ID
     * Находит активного покупателя по ID
     *
     * @param id customer identifier / идентификатор покупателя
     * @return Optional containing customer if found and active / Optional с покупателем, если найден и активен
     */
    Optional<Customer> findByIdAndActiveTrue(Long id);

    /**
     * Counts all active customers
     * Подсчитывает всех активных покупателей
     *
     * @return count of active customers / количество активных покупателей
     */
    long countByActiveTrue();

    /**
     * Checks if active customer exists with given ID
     * Проверяет существование активного покупателя с указанным ID
     *
     * @param id customer identifier / идентификатор покупателя
     * @return true if exists and active / true если существует и активен
     */
    boolean existsByIdAndActiveTrue(Long id);

    /**
     * Finds active customer with cart and positions eagerly loaded
     * Uses LEFT JOIN FETCH to load cart and positions in one query
     *
     * Находит активного покупателя с корзиной и позициями (жадная загрузка)
     * Использует LEFT JOIN FETCH для загрузки корзины и позиций одним запросом
     *
     * @param id customer identifier / идентификатор покупателя
     * @return Optional containing customer with loaded cart and positions / Optional с покупателем и загруженными корзиной и позициями
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.cart cart LEFT JOIN FETCH cart.positions WHERE c.id = :id AND c.active = true")
    Optional<Customer> findActiveCustomerWithCart(@Param("id") Long id);
}