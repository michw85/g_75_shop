package de.ait.g_75_shop.dto.mapping;
import de.ait.g_75_shop.domain.Position;
import de.ait.g_75_shop.dto.position.PositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PositionMapper {
    @Mapping(target = "totalPrice", expression = "java(entity.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity())))")
    PositionDto mapEntityToDto(Position entity);
}
