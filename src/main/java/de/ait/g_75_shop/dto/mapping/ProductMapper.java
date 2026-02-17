package de.ait.g_75_shop.dto.mapping;

import de.ait.g_75_shop.domain.Product;
import de.ait.g_75_shop.dto.product.ProductDto;
import de.ait.g_75_shop.dto.product.ProductSaveDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    ProductDto mapEntityToDto(Product entity);
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
