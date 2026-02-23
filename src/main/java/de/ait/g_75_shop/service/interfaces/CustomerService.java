package de.ait.g_75_shop.service.interfaces;
import de.ait.g_75_shop.domain.Customer;
import de.ait.g_75_shop.dto.customer.CustomerDto;
import de.ait.g_75_shop.dto.customer.CustomerSaveDto;
import de.ait.g_75_shop.dto.customer.CustomerUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    // Сохранить покупателя (автоматически активный)
//    Customer save(Customer customer);
    CustomerDto save(CustomerSaveDto saveDto);

    // Вернуть всех активных покупателей
//    List<Customer> getAllActiveCustomers();
    List<CustomerDto> getAllActiveCustomers();

    // Вернуть активного покупателя по ID
    Customer getActiveEntityById(Long id);
    CustomerDto getActiveCustomerById(Long id);

    // Изменить покупателя по ID
    void update(Long id, CustomerUpdateDto updateDto);
//    void update(Long id, Customer customer);

    // Удалить покупателя (soft delete)
    void deleteById(Long id);

    // Восстановить удаленного покупателя
    void restoreById(Long id);

    // Количество активных покупателей
    long getAllActiveCustomersCount();

    // Стоимость корзины покупателя
    BigDecimal getCustomerCartTotalCost(Long customerId);

    // Средняя стоимость продукта в корзине покупателя
    BigDecimal getCustomerCartAveragePrice(Long customerId);

    // Добавить товар в корзину
    void addProductToCart(Long customerId, Long productId, int quantity);

    // Удалить товар из корзины
    void removeProductFromCart(Long customerId, Long productId);

    // Очистить корзину
    void clearCart(Long customerId);

    // Добавление изображения к продукту
    void addImage(Long id, MultipartFile image) throws IOException;
}