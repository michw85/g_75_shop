package de.ait.g_75_shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Application Shop",
                description = "Application for various operations with Stores, Customers and Products",
                version = "1.0.0",
                contact = @Contact(
                        name = "Michael",
                        email = "michael@dxt.de",
                        url = "http://ait-tr.de"
                )
        )
)
public class SwaggerConfig {
}
