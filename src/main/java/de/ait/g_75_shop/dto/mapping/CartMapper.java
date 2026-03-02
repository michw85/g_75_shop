package de.ait.g_75_shop.dto.mapping;

import de.ait.g_75_shop.domain.Cart;
import de.ait.g_75_shop.dto.cart.CartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Cart entity to CartDto conversion
 * Calculates total price, average price, and total quantity on the fly
 *
 * MapStruct маппер для преобразования сущности Cart в CartDto
 * Вычисляет общую стоимость, среднюю цену и общее количество на лету
 */
@Mapper(componentModel = "spring", uses = {PositionMapper.class})
public interface CartMapper {

    /**
     * Maps Cart entity to CartDto with calculated fields
     *
     * Преобразует сущность Cart в CartDto с вычисляемыми полями
     *
     * @param entity Cart entity to map / сущность корзины для преобразования
     * @return CartDto with calculated values / CartDto с вычисленными значениями
     */
    @Mapping(target = "totalPrice", expression = "java(entity.getTotalPrice())")
    @Mapping(target = "averagePrice", expression = "java(entity.getAveragePrice())")
    @Mapping(target = "totalQuantity", expression = "java(entity.getPositions().stream().mapToInt(p -> p.getQuantity()).sum())")
    CartDto mapEntityToDto(Cart entity);
}