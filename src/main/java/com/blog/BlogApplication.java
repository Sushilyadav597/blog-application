package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for the RESTful Blog Application API.
 * 
 * Provides production-grade configurations, Spring Security with JWT authentication,
 * relational persistence with Hibernate and Spring Data JPA, and interactive
 * documentation via OpenAPI / Swagger UI.
 */
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}

