package com.andrei.plesoianu.sbecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${spring.app.env}")
    private String env;

    @Bean
    public OpenAPI customOpenApi() {
        SecurityScheme scheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token");

        SecurityRequirement bearerRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot eCommerce API - " + env)
                        .version("1.0")
                        .description("Collection of the exposed API endpoints and models involved")
                        .license(new License().name("Apache 2.0")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", scheme))
                .addSecurityItem(bearerRequirement)
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Dev Env"),
                        new Server()
                                .url("http://Prod.eba-8haqjvpm.eu-north-1.elasticbeanstalk.com/api/public")
                                .description("Prod Env")
                ));
    }
}
