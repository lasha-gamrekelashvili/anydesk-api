package com.example.anydeskapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Anydesk API",
        version = "1.0",
        description = "API for managing users and tasks"
    )
)
public class AnydeskApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnydeskApiApplication.class, args);
    }
}
