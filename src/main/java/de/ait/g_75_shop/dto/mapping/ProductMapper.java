package de.ait.g_75_shop.dto.mapping;

import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Product entity conversions
 * Handles mapping between Product entity and DTOs
 *
 * MapStruct маппер для преобразований сущности Product
 * Обрабатывает преобразования между сущностью Product и DTO
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Maps Product entity to ProductDto
     *
     * Преобразует сущность Product в ProductDto
     *
     * @param entity Product entity to map / сущность товара для преобразования
     * @return ProductDto / DTO товара
     */
    ProductDto mapEntityToDto(Product entity);
    /**
     * Maps ProductSaveDto to new Product entity
     * Ignores system fields (id, active)
     *
     * Преобразует ProductSaveDto в новую сущность Product
     * Игнорирует системные поля (id, active)
     *
     * @param dto DTO with product data / DTO с данными товара
     * @return new Product entity / новая сущность товара
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product mapDtoToEntity(ProductSaveDto dto);
//     public ProductDto mapEntityToDo(Product entity){
//        if(entity == null) {
//            return null;
//        }
//
//        ProductDto dto = new ProductDto();
//        dto.setId(entity.getId());
//        dto.setTitle(entity.getTitle());
//        dto.setPrice(entity.getPrice());
//
//        return dto;
//    }
}
