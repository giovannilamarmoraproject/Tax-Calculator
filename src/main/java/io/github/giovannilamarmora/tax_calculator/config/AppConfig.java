package io.github.giovannilamarmora.tax_calculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = "io.github.giovannilamarmora.utils")
@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Tax Calculator Swagger", version = "1.0.0"),
    servers = {
      @Server(
          url = "https://tax-calculator.giovannilamarmora.com",
          description = "Default Server URL"),
      @Server(url = "http://localhost:8080", description = "Local Server URL")
    })
public class AppConfig {}
