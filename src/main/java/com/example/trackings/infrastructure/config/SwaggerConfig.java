package com.example.trackings.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for the Trackings application.
 * Following DDD principles, this configuration is part of the infrastructure layer.
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:trackings}")
    private String applicationName;

    /**
     * Configures OpenAPI documentation for the Tracking Orders API.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI trackingOrdersOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tracking Orders API")
                        .description("RESTful API for managing and querying tracking orders. " +
                                    "Built with Spring Boot, following DDD and CQRS principles, " +
                                    "using reactive programming with WebFlux.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production server")
                ))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name("Tracking Orders")
                        .description("Operations related to tracking order management"));
    }
}