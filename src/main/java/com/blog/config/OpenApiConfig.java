package com.blog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger documentation configuration with JWT Bearer authentication scheme.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful Blog Application API")
                        .description("Industry-standard RESTful Blog Application API built with Java 21, Spring Boot 3, " +
                                "Spring Security 6, JWT Authentication, Hibernate, and MySQL 8.\n\n" +
                                "### How to Authenticate:\n" +
                                "1. Register an account via `POST /api/auth/register` or login via `POST /api/auth/login`.\n" +
                                "2. Copy the returned `token`.\n" +
                                "3. Click the **Authorize** button on the top right of this Swagger UI.\n" +
                                "4. Paste your token (the `Bearer ` prefix is attached automatically).\n" +
                                "5. Test all secured endpoints with role-based and ownership-based authorizations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Fresher Java Developer Portfolio")
                                .email("developer@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your valid JWT token to authenticate requests.")));
    }
}

