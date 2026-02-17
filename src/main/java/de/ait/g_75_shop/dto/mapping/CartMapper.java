package de.ait.g_75_shop.dto.mapping;

import de.ait.g_75_shop.domain.Cart;
import de.ait.g_75_shop.dto.cart.CartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {PositionMapper.class})
public interface CartMapper {

    @Mapping(target = "totalPrice", expression = "java(entity.getTotalPrice())")
    @Mapping(target = "averagePrice", expression = "java(entity.getAveragePrice())")
    @Mapping(target = "totalQuantity", expression = "java(entity.getPositions().stream().mapToInt(p -> p.getQuantity()).sum())")
    CartDto mapEntityToDto(Cart entity);
}