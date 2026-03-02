package de.ait.g_75_shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * OpenAPI (Swagger) configuration for API documentation
 * Defines API metadata like title, description, version, and contact information
 *
 * Конфигурация OpenAPI (Swagger) для документации API
 * Определяет метаданные API: название, описание, версию и контактную информацию
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Application Shop", // API title / Название API
                description = "Application for various operations with Stores, Customers and Products", // API description / Описание API
                version = "1.0.0", // API version / Версия API
                contact = @Contact(
                        name = "Michael", // Contact person name / Имя контактного лица
                        email = "michael@dxt.de", // Contact email / Email для связи
                        url = "http://ait-tr.de" // Contact URL / URL контакта
                )
        )
)
public class SwaggerConfig {
    // This class is intentionally empty as configuration is done via annotations
    // Этот класс намеренно пуст, так как конфигурация выполняется через аннотации
}
