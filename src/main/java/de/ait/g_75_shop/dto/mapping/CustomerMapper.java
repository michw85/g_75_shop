package de.ait.g_75_shop.dto.mapping;

import de.ait.g_75_shop.domain.Customer;
import de.ait.g_75_shop.dto.customer.CustomerDto;
import de.ait.g_75_shop.dto.customer.CustomerSaveDto;
import de.ait.g_75_shop.dto.customer.CustomerUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Customer entity conversions
 * Handles mapping between Customer entity and various DTOs
 *
 * MapStruct маппер для преобразований сущности Customer
 * Обрабатывает преобразования между сущностью Customer и различными DTO
 */
@Mapper(componentModel = "spring", uses = {CartMapper.class})
public interface CustomerMapper {

    /**
     * Maps Customer entity to CustomerDto
     *
     * Преобразует сущность Customer в CustomerDto
     *
     * @param entity Customer entity to map / сущность покупателя для преобразования
     * @return CustomerDto / DTO покупателя
     */
    CustomerDto mapEntityToDto(Customer entity);

    /**
     * Maps CustomerSaveDto to new Customer entity
     * Ignores system fields (id, active, cart)
     *
     * Преобразует CustomerSaveDto в новую сущность Customer
     * Игнорирует системные поля (id, active, cart)
     *
     * @param saveDto DTO with customer data / DTO с данными покупателя
     * @return new Customer entity / новая сущность покупателя
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cart", ignore = true)
    Customer mapDtoToEntity(CustomerSaveDto saveDto);

    /**
     * Updates existing Customer entity with data from CustomerUpdateDto
     *
     * Обновляет существующую сущность Customer данными из CustomerUpdateDto
     *
     * @param entity Customer entity to update / сущность покупателя для обновления
     * @param updateDto DTO with new data / DTO с новыми данными
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cart", ignore = true)
    void updateEntity(@MappingTarget Customer entity, CustomerUpdateDto updateDto);
}