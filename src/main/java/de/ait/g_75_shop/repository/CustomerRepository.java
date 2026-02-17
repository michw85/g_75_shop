package de.ait.g_75_shop.repository;

import de.ait.g_75_shop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Все активные покупатели
    List<Customer> findAllByActiveTrue();

    // Активный покупатель по ID
    Optional<Customer> findByIdAndActiveTrue(Long id);

    // Количество активных покупателей
    long countByActiveTrue();

    // Проверка активности покупателя
    boolean existsByIdAndActiveTrue(Long id);

    // Получить покупателя с его корзиной и позициями
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.cart cart LEFT JOIN FETCH cart.positions WHERE c.id = :id AND c.active = true")
    Optional<Customer> findActiveCustomerWithCart(@Param("id") Long id);
}