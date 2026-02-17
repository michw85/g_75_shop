package de.ait.g_75_shop.dto.mapping;

import de.ait.g_75_shop.domain.Customer;
import de.ait.g_75_shop.dto.customer.CustomerDto;
import de.ait.g_75_shop.dto.customer.CustomerSaveDto;
import de.ait.g_75_shop.dto.customer.CustomerUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CartMapper.class})
public interface CustomerMapper {

    CustomerDto mapEntityToDto(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cart", ignore = true)
    Customer mapDtoToEntity(CustomerSaveDto saveDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cart", ignore = true)
    void updateEntity(@MappingTarget Customer entity, CustomerUpdateDto updateDto);
}