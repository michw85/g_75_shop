package de.ait.g_75_shop.dto.mapping;
import de.ait.g_75_shop.domain.Position;
import de.ait.g_75_shop.dto.position.PositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Position entity to PositionDto conversion
 * Calculates total price for the position
 *
 * MapStruct маппер для преобразования сущности Position в PositionDto
 * Вычисляет общую стоимость позиции
 */

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PositionMapper {
    /**
     * Maps Position entity to PositionDto with calculated total price
     *
     * Преобразует сущность Position в PositionDto с вычисленной общей стоимостью
     *
     * @param entity Position entity to map / сущность позиции для преобразования
     * @return PositionDto with calculated total price / PositionDto с вычисленной общей стоимостью
     */
    @Mapping(target = "totalPrice", expression = "java(entity.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity())))")
    PositionDto mapEntityToDto(Position entity);
}
