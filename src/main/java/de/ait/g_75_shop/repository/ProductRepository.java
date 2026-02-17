package de.ait.g_75_shop.repository;

import de.ait.g_75_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/*
Как работает технология Spring Data JPA:
1. При старте приложения технология сканирует наш класс Product и его поля.
2. Зная класс и поля, она "понимает", как с этим работать в БД.
3. Она сама пишет класс, который реализует наш интерфейс ProductRepository.
4. В этом классе она сама пишет стандартные методы по работе с БД, например findAll, findById, save...
И в этих методах она сама прописывает нужные SQL запросы в БД.
5. Spring создаёт объект этого класса, который написала технология JPA, и помещает его в Spring Context.
 */

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(Long id);
    long countByActiveTrue();
    boolean existsByIdAndActiveTrue(Long id);
}
